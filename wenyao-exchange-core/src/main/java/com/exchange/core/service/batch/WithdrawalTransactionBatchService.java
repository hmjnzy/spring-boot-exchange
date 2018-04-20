package com.exchange.core.service.batch;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.*;
import com.exchange.core.domain.enums.*;
import com.exchange.core.domain.vo.TransactionVo;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.provider.MqPublisher;
import com.exchange.core.repository.dao.ManualTransactionDao;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.TransactionService;
import com.exchange.core.service.WalletService;
import com.exchange.core.util.CompareUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
public class WithdrawalTransactionBatchService {

    private final CoinService coinService;
    private final WalletService walletService;
    private final MqPublisher mqPublisher;
    private final TransactionService transactionService;
    private final ManualTransactionDao manualTransactionDao;

    @Autowired
    public WithdrawalTransactionBatchService(CoinService coinService, WalletService walletService, MqPublisher mqPublisher,
                                             TransactionService transactionService, ManualTransactionDao manualTransactionDao) {
        this.coinService = coinService;
        this.walletService = walletService;
        this.mqPublisher = mqPublisher;
        this.transactionService = transactionService;
        this.manualTransactionDao = manualTransactionDao;
    }

    @SoftTransational
    public void doPublishTransaction(final CoinEnum coinEnum) {
        if (coinService.findOneByNameAndActiveToCount(coinEnum, ActiveEnum.Y) == 0) {
            log.warn("[doPublishTransaction] Coin status is deactive {}", coinEnum);
        }

        final TransactionVo.ReqTransactions reqTransactions = new TransactionVo.ReqTransactions(CategoryEnum.send, coinEnum, StatusEnum.APPROVAL, 1, 100);
        final PageInfo<Transaction> transactionsPage = transactionService.getTransactions(reqTransactions);

        for (Transaction transaction : transactionsPage.getList()) {
            log.info("[doPublishTransaction] {}", transaction);
            final WalletVo.TransactionInfo transactionInfo = new WalletVo.TransactionInfo();
            transactionInfo.setId(transaction.getId());
            transactionInfo.setUserId(transaction.getUserId());
            transactionInfo.setCoinName(coinEnum);
            transactionInfo.setAddress(transaction.getAddress());
            transactionInfo.setCategory(transaction.getCategory());
            transactionInfo.setBankNm(transaction.getBankNm());
            transactionInfo.setRecvNm(transaction.getRecvNm());
            mqPublisher.withdrawalTransaction(transactionInfo);
        }
    }

    @HardTransational
    public void doTransaction(final WalletVo.TransactionInfo transactionInfo) throws Exception {
        if (coinService.findOneByNameAndActiveToCount(transactionInfo.getCoinName(), ActiveEnum.Y) == 0) {
            log.error("[doTransaction] Coin is not active or exist");
            return;
        }

        final Wallet wallet = walletService.findOneByUserIdAndCoinToBalance(transactionInfo.getUserId(), transactionInfo.getCoinName());
        if (wallet == null) {
            log.warn("[doTransaction] Not Exist Wallet: {} {}", transactionInfo.getUserId(), transactionInfo.getCoinName());
            return;
        }

        final Transaction existTransaction = transactionService.findOneByIdToRegDtAndStatus(transactionInfo.getId(), transactionInfo.getUserId(), transactionInfo.getCoinName());
        if (existTransaction == null) {
            log.warn("[doTransaction] Not Exist Transaction: {} {} {}", transactionInfo.getId(), transactionInfo.getUserId(), transactionInfo.getCoinName());
            return;
        }

        if (existTransaction != null && (
                        !StatusEnum.APPROVAL.equals(existTransaction.getStatus()) || existTransaction.getCompleteDtm() != null
        )) {
            log.warn("[doTransaction] Exist Transaction: {} {} {}", transactionInfo.getId(), transactionInfo.getUserId(), transactionInfo.getCoinName());
            return;
        }

        final ManualTransaction existManualTransaction = manualTransactionDao.findByIdAndUserIdToRegDt(transactionInfo.getId());
        if (existManualTransaction == null) {
            log.warn("[doTransaction] Not Exist ManualTransaction: {} {} {}", transactionInfo.getId(), transactionInfo.getUserId(), transactionInfo.getCoinName());
            return;
        }

        wallet.setUsingBalance(wallet.getUsingBalance().subtract(existTransaction.getAmount()));
        walletService.updateByIdToUsingBalance(wallet);

        String txId;
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(existTransaction.getAmount(), existTransaction.getWithdrawalFeeAmount())) ||
                CompareUtil.Condition.EQ.equals(CompareUtil.compare(existTransaction.getAmount(), existTransaction.getWithdrawalFeeAmount()))
        ) {//<=提现费率

            txId = "After subtracting the rate, 0";

            log.warn("[doTransaction] 提现扣除提现费率后剩余<=0: {} {} {}", transactionInfo.getId(), transactionInfo.getUserId(), transactionInfo.getCoinName());
        } else {

            //final BigDecimal minFee = walletService.getMinFee(transactionInfo.getCoinName());
            final BigDecimal sendingAmount = existTransaction.getAmount()
                    .subtract(existTransaction.getWithdrawalFeeAmount())
                    .setScale(8, RoundingMode.DOWN);

            final AdminWallet adminWallet = walletService.decreaseAvailableAdminBalance(transactionInfo.getCoinName(), WalletType.HOT, sendingAmount);

            log.info("-提现- CoinName:{}, from Address:{}, to Address:{}, Amount:{}, tx Fee:{}",
                    transactionInfo.getCoinName(),
                    adminWallet.getAddress(),
                    transactionInfo.getAddress(),
                    sendingAmount.toPlainString(),
                    existTransaction.getWithdrawalFeeAmount()
            );
            //send from admin hot wallet
            txId = walletService.sendFromHotWallet(
                    transactionInfo.getCoinName(),
                    adminWallet.getAddress(),
                    transactionInfo.getAddress(),
                    sendingAmount,
                    existTransaction.getWithdrawalFeeAmount()
            );

            log.info("https://www.etherchain.org/tx/{}", txId);
        }

        manualTransactionDao.updateCompleteStatus(
                transactionInfo.getId(),
                transactionInfo.getUserId(),
                StatusEnum.COMPLETED,
                existManualTransaction.getRegDt() == null ? LocalDateTime.now() : existManualTransaction.getRegDt()
        );

        transactionService.updateCompleteStatus(
                transactionInfo.getId(),
                txId,
                StatusEnum.COMPLETED,
                BigInteger.ONE,
                LocalDateTime.now(),
                existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt()
        );
    }

}
