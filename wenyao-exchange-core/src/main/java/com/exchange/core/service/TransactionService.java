package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.domain.dto.*;
import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import com.exchange.core.domain.vo.Response;
import com.exchange.core.domain.vo.TransactionVo;
import com.exchange.core.exception.ExchangeException;
import com.exchange.core.repository.dao.ManualTransactionDao;
import com.exchange.core.repository.dao.TransactionDao;
import com.exchange.core.util.CompareUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    private final TransactionDao transactionDao;
    private final ManualTransactionDao manualTransactionDao;
    private final UserService userService;
    private final WalletService walletService;
    private final CoinService coinService;
    private final OtpService otpService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionService(TransactionDao transactionDao, ManualTransactionDao manualTransactionDao, UserService userService, WalletService walletService,
                              CoinService coinService, OtpService otpService, ModelMapper modelMapper) {
        this.manualTransactionDao = manualTransactionDao;
        this.transactionDao = transactionDao;
        this.userService = userService;
        this.walletService = walletService;
        this.coinService = coinService;
        this.otpService = otpService;
        this.modelMapper = modelMapper;
    }

    public PageInfo<Transaction> getTransactions(final Long userId, final TransactionVo.ReqTransactions reqTransactions) {
        final List<Transaction> transactions = this.transactionDao.findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc(userId, reqTransactions);
        final PageInfo<Transaction> pageInfo =  new PageInfo<>(transactions);
        return pageInfo;
    }

    public PageInfo<Transaction> getTransactions(final TransactionVo.ReqTransactions reqTransactions) {
        final List<Transaction> transactions = this.transactionDao.findAllByCoinAndCategoryAndStatusOrderByRegDtASC(reqTransactions);
        final PageInfo<Transaction> pageInfo =  new PageInfo<>(transactions);
        return pageInfo;
    }

    public Transaction findOneByCoinAndTxId(final CoinEnum coinName, final String txId) {
        return this.transactionDao.findOneByCoinAndTxId(coinName, txId);
    }

    public Transaction findOneByIdToRegDtAndStatus(final String id, final Long userId, final CoinEnum coinName) {
        return this.transactionDao.findOneByIdToRegDtAndStatus(id, userId, coinName);
    }

    public void updateCompleteStatus(final String id, final String txId, final StatusEnum status, final BigInteger confirmation, final LocalDateTime completeDtm, final LocalDateTime regDt) {
        this.transactionDao.updateCompleteStatus(id, txId, status, confirmation, completeDtm, regDt);
    }

    public int insertTransaction(final Transaction transaction) {
        return this.transactionDao.insert(transaction);
    }

    @HardTransational
    public CodeEnum register(final ManualTransaction manualTransaction) {

        final User cacheUser = userService.findOneByIdToAllFields(manualTransaction.getUserId());
        if (!otpService.isOtpValid(cacheUser.getOtpHash(), manualTransaction.getAuthCode())) {
            return CodeEnum.USER_OTP_AUTH_FAIL;
        }

        final Wallet wallet = walletService.findOneByUserIdAndCoinToBalance(manualTransaction.getUserId(), manualTransaction.getCoinName());
        if (wallet == null) {
            return CodeEnum.WALLET_NOT_EXIST;
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(wallet.getAvailableBalance(), manualTransaction.getAmount()))) {
            return CodeEnum.AVAILABLE_BALANCE_NOT_ENOUGH;
        }

        final Coin coin = coinService.findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount(manualTransaction.getCoinName());
        if (CompareUtil.Condition.GT.equals(CompareUtil.compare(coin.getWithdrawalMinAmount(), manualTransaction.getAmount()))) {
            return CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT;
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(manualTransaction.getAmount()));
        wallet.setUsingBalance(wallet.getUsingBalance().add(manualTransaction.getAmount()));
        wallet.setTodayWithdrawalTotalBalance(wallet.getTodayWithdrawalTotalBalance().add(manualTransaction.getAmount()));
        walletService.updateByIdToAllBalance(wallet);

        final Transaction transaction = new Transaction();
        transaction.setId(manualTransaction.getId());
        transaction.setUserId(manualTransaction.getUserId());
        if (this.transactionDao.findByIdAndUserIdToCount(transaction) > 0) {
            return CodeEnum.ALREADY_TRANSACTION_EXIST;
        }

        final Transaction newTransaction = modelMapper.map(manualTransaction, Transaction.class);
        newTransaction.setConfirmation(BigInteger.ZERO);
        newTransaction.setWithdrawalFeeAmount(coin.getWithdrawalFeeAmount());
        transactionDao.insert(newTransaction);

        if (manualTransactionDao.findByIdAndUserIdToCount(manualTransaction) > 0) {
            return CodeEnum.ALREADY_MANUAL_TRANSACTION_EXIST;
        }

        manualTransaction.setWithdrawalFeeAmount(coin.getWithdrawalFeeAmount());
        manualTransactionDao.insert(manualTransaction);

        return CodeEnum.SUCCESS;
    }
//
//    @SoftTransational
//    public ManualTransaction registManualTransaction(ManualTransaction manualTransaction) {
//        ManualTransactionPK manualTransactionPK = new ManualTransactionPK();
//        manualTransactionPK.setId(manualTransaction.getId());
//        manualTransactionPK.setUserId(manualTransaction.getUserId());
//        ManualTransaction existManualTransaction = manualTransactionRepository.findOne(manualTransactionPK);
//        if (existManualTransaction != null) {
//            throw new ExchangeException(CodeEnum.ALREADY_MANUAL_TRANSACTION_EXIST);
//        }
//
//        return manualTransactionRepository.save(manualTransaction);
//    }
//
//    @SoftTransational
//    public void updateStatusManualTransaction(ManualTransactionPK manualTransactionPK, StatusEnum status, String reason) {
//        ManualTransaction manualTransaction = manualTransactionRepository.findOne(manualTransactionPK);
//        if (manualTransaction == null) {
//            throw new ExchangeException(CodeEnum.MANUAL_TRANSACTION_NOT_EXIST);
//        }
//
//        if (!StatusEnum.PENDING.equals(manualTransaction.getStatus())) {
//            throw new ExchangeException(CodeEnum.ALREADY_STATUS_IS_NOT_PENDING);
//        }
//
//        manualTransaction.setStatus(status);
//        manualTransaction.setReason(reason);
//    }
//
//    @SoftTransational
//    public Transaction registTransaction(Transaction transaction) {
//        TransactionPK transactionPK = new TransactionPK();
//        transactionPK.setId(transaction.getId());
//        transactionPK.setUserId(transaction.getUserId());
//        Transaction existTransaction = transactionRepository.findOne(transactionPK);
//        if (existTransaction != null) {
//            throw new ExchangeException(CodeEnum.ALREADY_TRANSACTION_EXIST);
//        }
//
//        return transactionRepository.save(transaction);
//    }
//
//    @SoftTransational
//    public void updateStatusTransaction(TransactionPK transactionPK, StatusEnum status, String reason) {
//        Transaction transaction = transactionRepository.findOne(transactionPK);
//        if (transaction == null) {
//            throw new ExchangeException(CodeEnum.TRANSACTION_NOT_EXIST);
//        }
//
//        if (!StatusEnum.PENDING.equals(transaction.getStatus())) {
//            throw new ExchangeException(CodeEnum.ALREADY_STATUS_IS_NOT_PENDING);
//        }
//
//        transaction.setStatus(status);
//        transaction.setReason(reason);
//    }

}
