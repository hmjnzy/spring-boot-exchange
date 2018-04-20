package com.exchange.core.service.batch;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.AdminWallet;
import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.Transaction;
import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.*;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.provider.MqPublisher;
import com.exchange.core.provider.wallet.SimpleWalletRpcProvider;
import com.exchange.core.provider.wallet.WalletRpcProviderFactory;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.TransactionService;
import com.exchange.core.service.WalletService;
import com.exchange.core.util.KeyGenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DepositTransactionBatchService {

    private final CoinService coinService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final WalletRpcProviderFactory walletRpcProviderFactory;
    private final MqPublisher mqPublisher;

    @Autowired
    public DepositTransactionBatchService(CoinService coinService, WalletService walletService, TransactionService transactionService,
                                          WalletRpcProviderFactory walletRpcProviderFactory, MqPublisher mqPublisher) {
        this.coinService = coinService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.walletRpcProviderFactory = walletRpcProviderFactory;
        this.mqPublisher = mqPublisher;
    }

    @SoftTransational
    public void doPublishTransaction(final CoinEnum coinEnum) throws Exception {

        final Coin coin = coinService.findOneByNameAndActiveToPageInfo(coinEnum, ActiveEnum.Y);
        if (coin == null) {
            log.warn("[doPublishTransaction] Coin status is deactive {}", coinEnum);
            return;
        }

        log.info("[doPublishTransaction] Coin {}, Offset {}, PageSize {}", coinEnum, coin.getDepositScanPageOffset(), coin.getDepositScanPageSize());

        final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        final SimpleWalletRpcProvider.Page page = new SimpleWalletRpcProvider.Page();
        page.setPageNo(coin.getDepositScanPageOffset());
        page.setPageSize(coin.getDepositScanPageSize());

        final List<WalletVo.TransactionInfo> transactionInfos = simpleWalletRpcProvider.getTransactions(CategoryEnum.receive, page);
        final int offset = transactionInfos.size();
        if (offset > 0) {
            transactionInfos.stream().forEach(transactionInfo -> {
                final Wallet wallet = walletService.findOneByCoinAndAddressToUserId(coinEnum, transactionInfo.getAddress());
                if (wallet != null) {
                    transactionInfo.setUserId(wallet.getUserId());
                    transactionInfo.setCoinName(coinEnum);

                    log.info("------++++++-------: {}", transactionInfo);

                    //mqPublisher.depositTransaction(transactionInfo);
                } else {
                    log.warn("[doPublishTransaction] Wallet is not exist {}", transactionInfo.getAddress());
                }
            });
        }

        if (offset > 0) {
            coin.setDepositScanPageOffset(coin.getDepositScanPageOffset() + offset);
        } else {
            coin.setDepositScanPageOffset(coin.getDepositScanPageSize());
        }

        log.info("[doPublishTransaction] Offset {}, PageSize {}, transactionInfos {}",
                coin.getDepositScanPageOffset(),
                coin.getDepositScanPageSize(),
                transactionInfos);
/* TODO
* 1.判断 开始 和 结束 出现 负数（-）现象
* 2.如总快1000，开始990，结束1010 时 接收到 实际和平台有关的 2条 ，有10个block null 由于超出总的 最总需要 更新 Coin DepositScanPageOffset。
* 目前 还未 整理 其他币种的 判断逻辑 所以 暂时 放着充值 先做 交易。
* */

    }

    @HardTransational
    public void doTransaction(final WalletVo.TransactionInfo transactionInfo) throws Exception {
        final Wallet wallet = walletService.findOneByUserIdAndCoin(transactionInfo.getUserId(), transactionInfo.getCoinName());
        if (wallet == null) {
            log.warn("[doTransaction] Not Exist Wallet: {} {} {}",
                    transactionInfo.getUserId(),
                    transactionInfo.getCoinName(),
                    transactionInfo.getTxId());
            return;
        }

        final Transaction existTransaction = transactionService.findOneByCoinAndTxId(transactionInfo.getCoinName(), transactionInfo.getTxId());
        if (existTransaction != null && !StatusEnum.PENDING.equals(existTransaction.getStatus())) {
            log.warn("[doTransaction] Exist Transaction: {} {} {} {}",
                    existTransaction.getStatus(),
                    transactionInfo.getUserId(),
                    transactionInfo.getCoinName(),
                    transactionInfo.getTxId());
            return;
        }

        final Coin coin = coinService.findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation(transactionInfo.getCoinName());
        final int iConfirmation = coin.getDepositAllowConfirmation().compareTo(transactionInfo.getConfirmations());//<=
        if (iConfirmation == -1 || iConfirmation == 0) {
            if (existTransaction != null) {
                transactionService.updateCompleteStatus(
                        existTransaction.getId(),
                        null,//不修改txId
                        StatusEnum.COMPLETED,
                        transactionInfo.getConfirmations(),
                        LocalDateTime.now(),
                        existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt()
                );
            } else {
                Transaction transaction = new Transaction();
                transaction.setId(KeyGenUtil.generateTransactionId());
                transaction.setUserId(transactionInfo.getUserId());
                transaction.setCategory(transactionInfo.getCategory());
                transaction.setCoinName(transactionInfo.getCoinName());
                transaction.setTxId(transactionInfo.getTxId());
                transaction.setAddress(transactionInfo.getAddress());
                transaction.setAmount(transactionInfo.getAmount());
                transaction.setRegDt(LocalDateTime.now());
                transaction.setCompleteDtm(LocalDateTime.now());
                transaction.setConfirmation(transactionInfo.getConfirmations());
                transaction.setStatus(StatusEnum.COMPLETED);
                transactionService.insertTransaction(transaction);
            }

            wallet.setAvailableBalance(wallet.getAvailableBalance().add(transactionInfo.getAmount()));
            walletService.updateByUserIdAndCoinToAvailableBalance(wallet);

            //send to admin cold wallet
            final BigDecimal realTotalBalance = walletService.getRealWalletTotalBalance(transactionInfo.getCoinName(), transactionInfo.getUserId());
            final BigDecimal sendingAmount = realTotalBalance.setScale(8, RoundingMode.DOWN);
            final int iAutoCollectMinAmount = coin.getAutoCollectMinAmount().compareTo(realTotalBalance);//<=
            if (iAutoCollectMinAmount == -1 || iAutoCollectMinAmount == 0 && sendingAmount.compareTo(BigDecimal.ZERO) == 1) {

                final AdminWallet adminWallet = walletService.increaseAvailableAdminBalance(transactionInfo.getCoinName(), WalletType.COLD, sendingAmount);

                log.info("-充值- CoinName:{}, from Address(AdminWallet):{}, to Address:{}, Amount:{}, real Total Balance:{}",
                        transactionInfo.getCoinName(),
                        adminWallet.getAddress(),
                        transactionInfo.getAddress(),
                        sendingAmount.toPlainString(),
                        realTotalBalance.toPlainString()
                );

                final String txId = walletService.sendTo(
                        transactionInfo.getCoinName(),
                        transactionInfo.getUserId(),
                        wallet.getAddress(),
                        adminWallet.getAddress(),
                        sendingAmount,
                        BigDecimal.ZERO
                );

                log.info("https://www.etherchain.org/tx/{}", txId);
            }
        } else {
            if (existTransaction != null) {
                transactionService.updateCompleteStatus(
                        existTransaction.getId(),
                        null,//不修改txId
                        StatusEnum.PENDING,
                        transactionInfo.getConfirmations(),
                        null,
                        existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt()
                );
            } else {
                final Transaction transaction = new Transaction();
                transaction.setId(KeyGenUtil.generateTransactionId());
                transaction.setUserId(transactionInfo.getUserId());
                transaction.setCategory(transactionInfo.getCategory());
                transaction.setCoinName(transactionInfo.getCoinName());
                transaction.setTxId(transactionInfo.getTxId());
                transaction.setAddress(transactionInfo.getAddress());
                transaction.setAmount(transactionInfo.getAmount());
                transaction.setRegDt(LocalDateTime.now());
                transaction.setCompleteDtm(null);
                transaction.setConfirmation(transactionInfo.getConfirmations());
                transaction.setStatus(StatusEnum.PENDING);
                transactionService.insertTransaction(transaction);
            }
        }
    }

}
