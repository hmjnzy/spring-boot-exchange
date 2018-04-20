package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.*;
import com.exchange.core.domain.enums.*;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.exception.ExchangeException;
import com.exchange.core.provider.wallet.SimpleWalletRpcProvider;
import com.exchange.core.provider.wallet.WalletRpcProviderFactory;
import com.exchange.core.repository.dao.AdminWalletDao;
import com.exchange.core.repository.dao.LevelDao;
import com.exchange.core.repository.dao.WalletDao;
import com.exchange.core.util.CalculateFeeUtil;
import com.exchange.core.util.CompareUtil;
import com.exchange.core.util.DateUtil;
import com.exchange.core.util.WalletUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    private final WalletDao walletDao;
    private final AdminWalletDao adminWalletDao;
    private final LevelDao levelDao;
    private final CoinService coinService;
    private final TradeCoinService tradeCoinService;
    private final WalletRpcProviderFactory walletRpcProviderFactory;

    @Autowired
    public WalletService(WalletDao walletDao, AdminWalletDao adminWalletDao, LevelDao levelDao, CoinService coinService, TradeCoinService tradeCoinService,
                         WalletRpcProviderFactory walletRpcProviderFactory) {
        this.walletDao = walletDao;
        this.adminWalletDao = adminWalletDao;
        this.levelDao = levelDao;
        this.coinService = coinService;
        this.tradeCoinService = tradeCoinService;
        this.walletRpcProviderFactory = walletRpcProviderFactory;
    }

    public int findByUserIdAndCoinToCount(final Long userId, final CoinEnum coinEnum) {
        return walletDao.findByUserIdAndCoinToCount(userId, coinEnum);
    }

    @SoftTransational
    public WalletVo.ResCreateWallet preCreateWallet(final Long userId, final CoinEnum coinEnum) {

        if (this.findByUserIdAndCoinToCount(userId, coinEnum) == 0) {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setCoinName(coinEnum);
            wallet.setAddress(null);
            wallet.setBankCode(null);
            wallet.setBankName(null);
            wallet.setRecvCorpNm(null);
            wallet.setTag(null);

            //TODO 币币交易时 可能不需要此 处理 以后在判断
//            if (CoinEnum.USDT.equals(coinEnum)) {
//                wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
//            }

            wallet.setAvailableBalance(BigDecimal.ZERO);
            wallet.setUsingBalance(BigDecimal.ZERO);
            wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
            wallet.setRegDt(LocalDateTime.now());

            walletDao.insert(wallet);
        }

        return WalletVo.ResCreateWallet
                .builder()
                .address(null)
                .tag(null)
                .build();
    }

    public Wallet findOneByUserIdAndCoin(final Long userId, final CoinEnum coinEnum) {
        return walletDao.findOneByUserIdAndCoin(userId, coinEnum);
    }

    public Wallet findOneByUserIdAndCoinToBalance(final Long userId, final CoinEnum coinEnum) {
        return walletDao.findOneByUserIdAndCoinToBalance(userId, coinEnum);
    }

    public int updateByIdToAllBalance(final Wallet wallet) {
        return walletDao.updateByIdToAllBalance(wallet);
    }

    public int updateByIdToUsingBalance(final Wallet wallet) {
        return walletDao.updateByIdToUsingBalance(wallet);
    }

    public int updateByIdToAvailableBalance(final Wallet wallet) {
        return walletDao.updateByIdToAvailableBalance(wallet);
    }

    public int updateByUserIdAndCoinToAvailableBalance(final Wallet wallet) {
        return walletDao.updateByUserIdAndCoinToAvailableBalance(wallet);
    }

    public List<Wallet> findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId(Long userId, BigDecimal todayWithdrawalTotalBalance) {
        return walletDao.findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId(userId, todayWithdrawalTotalBalance);
    }

    public List<Wallet> findAllByCoinToAddress(final CoinEnum coinEnum) {
        return walletDao.findAllByCoinToAddress(coinEnum);
    }

    public Wallet findOneByCoinAndAddressToUserId(final CoinEnum coinEnum, final String address) {
        return walletDao.findOneByCoinAndAddressToUserId(coinEnum, address);
    }

    @HardTransational
    public int updateByIdToTodayWithdrawalTotalBalance(final Wallet wallet) {
        return walletDao.updateByIdToTodayWithdrawalTotalBalance(wallet);
    }

    @HardTransational
    public void decreaseUsingBalance(final Long userId, final CoinEnum coinEnum, final BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        final Wallet wallet = walletDao.findOneByUserIdAndCoinToUsingBalance(userId, coinEnum);
        if (wallet == null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        /*
        * 减去已用ETH后 如超出已用ETH个数 抛异常
        * */
        final BigDecimal currentUsingBalance = wallet.getUsingBalance().subtract(amount);
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(currentUsingBalance))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }
        wallet.setUsingBalance(currentUsingBalance);
        this.updateByIdToUsingBalance(wallet);
    }

    @HardTransational
    public void increaseAvailableBalance(final Long userId, final CoinEnum coinEnum, final BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        final Wallet wallet = walletDao.findOneByUserIdAndCoinToAvailableBalance(userId, coinEnum);
        if (wallet == null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        this.updateByIdToAvailableBalance(wallet);
    }

    @HardTransational
    public void  cancelBalance(final Long userId, final CoinEnum coinEnum, final BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        final Wallet wallet = walletDao.findOneByUserIdAndCoinToUsingBalanceAndAvailableBalance(userId, coinEnum);
        if (wallet == null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        final BigDecimal usingBalance = wallet.getUsingBalance().subtract(amount);
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(usingBalance))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        wallet.setUsingBalance(usingBalance);
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        this.walletDao.updateByIdToUsingBalanceAndAvailableBalance(wallet);
    }

    public MyHistoryOrder increaseAvailableBalanceAndFeeBalance(final Order order, final Order toOrder, final CoinEnum coinEnum,
                                                                final BigDecimal coinTradingFeePercent, final BigDecimal amount, final BigDecimal price) {
        final MyHistoryOrder myHistoryOrder = new MyHistoryOrder();
        myHistoryOrder.setUserId(order.getUserId());
        myHistoryOrder.setOrderId(order.getId());
        myHistoryOrder.setAmount(amount);
        myHistoryOrder.setCompletedDtm(LocalDateTime.now());
        myHistoryOrder.setDt(DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(LocalDateTime.now()));
        myHistoryOrder.setOrderType(order.getOrderType());
        myHistoryOrder.setPrice(price);//单价
        myHistoryOrder.setRegDtm(LocalDateTime.now());
        myHistoryOrder.setStatus(OrderStatus.COMPLETED);
        myHistoryOrder.setCoinPair(order.getCoinPair());
        myHistoryOrder.setFromCoinName(order.getFromCoinName());
        myHistoryOrder.setToCoinName(order.getToCoinName());
        myHistoryOrder.setToUserId(toOrder.getUserId());
        myHistoryOrder.setToOrderId(toOrder.getId());
        if (OrderType.SELL.equals(order.getOrderType())) {
            //给操作者的USDT钱包++
            this.increaseAvailableBalance(
                    order.getUserId(),//卖出人编号
                    coinEnum,//USDT
                    CalculateFeeUtil.getRealAmount(
                            WalletUtil.scale(amount.multiply(price)),
                            coinTradingFeePercent//USDT的费率
                    )
            );
            //Admin USDT COLD Wallet钱包++
            this.increaseAvailableAdminBalance(
                    coinEnum,
                    WalletType.COLD,
                    CalculateFeeUtil.getFeeAmount(
                            WalletUtil.scale(amount.multiply(price)),
                            coinTradingFeePercent
                    )
            );

            return myHistoryOrder;
        } else if (OrderType.BUY.equals(order.getOrderType())) {
            this.increaseAvailableBalance(
                    order.getUserId(),//买入人编号
                    coinEnum,//ETH
                    CalculateFeeUtil.getRealAmount(
                            WalletUtil.scale(amount),
                            coinTradingFeePercent//ETH的费率
                    )
            );
            //Admin ETH COLD Wallet钱包++
            this.increaseAvailableAdminBalance(
                    coinEnum,
                    WalletType.COLD,
                    CalculateFeeUtil.getFeeAmount(
                            WalletUtil.scale(amount),
                            coinTradingFeePercent
                    )
            );

            return myHistoryOrder;
        } else {
            throw new ExchangeException(CodeEnum.INVALID_ORDER_TYPE);
        }

    }

    public WalletVo.WalletInfos getMyWalletsTradeCoin(final Long userId, final CoinPairEnum coinPairEnum) {

        final List<String> conins = tradeCoinService.splitCoinPair(coinPairEnum);
        final List<WalletVo.WalletInfos.Info> infos = new ArrayList<>();
        for (String coinName : conins) {

            final Coin coin = coinService.findOneByNameToAllFields(coinName);
            Wallet wallet = null;
            if (userId > 0 && coin != null) {
                wallet = this.findOneByUserIdAndCoin(userId, coin.getName());
            }

            infos.add(
                    WalletVo.WalletInfos.Info.builder()
                            .coin(coin)
                            .wallet(wallet)
                            .build()
            );
        }

        return WalletVo.WalletInfos.builder()
                .infos(infos)
                .build();
    }

    public WalletVo.CoinList getMyWalletsOfDeposit(final Long userId, final CoinEnum coinEnum) {

        Wallet wallet = null;
        final List<WalletVo.CoinList.Coins> coinList = new ArrayList<>();
        final List<Coin> coins = coinService.findAllToName();
        for (Coin coin : coins) {

            if (coin.getName().equals(coinEnum)) {

                wallet = this.findOneByUserIdAndCoin(userId, coin.getName());
            }

            coinList.add(
                    WalletVo.CoinList.Coins.builder()
                            .coin(coin)
                            .build()
            );
        }

        return WalletVo.CoinList.builder()
                .coins(coinList)
                .currentWallet(wallet)
                .build();
    }

    public WalletVo.CoinList getMyWalletsOfWithdrawal(final Long userId, final LevelEnum level, final CoinEnum coinEnum) {

        Coin currentCoin = null;
        Wallet currentWallet = null;
        Level currentLevel = null;
        final List<WalletVo.CoinList.Coins> coinList = new ArrayList<>();
        final List<Coin> coins = coinService.findAllToName();
        for (Coin coin : coins) {

            if (coin.getName().name().equals(coinEnum.name())) {
                currentCoin = coinService.findOneByNameToAllFields(coin.getName().name());
                currentWallet = this.findOneByUserIdAndCoin(userId, coin.getName());
                currentLevel = levelDao.findOneByCoinNameAndLevel(coinEnum, level);
            }

            coinList.add(
                    WalletVo.CoinList.Coins.builder()
                            .coin(coin)
                            .build()
            );
        }

        return WalletVo.CoinList.builder()
                .coins(coinList)
                .currentCoin(currentCoin)
                .currentWallet(currentWallet)
                .currentLevel(currentLevel)
                .build();
    }

    @SoftTransational
    public WalletVo.ResCreateWallet createWallet(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = this.findOneByUserIdAndCoin(userId, coinEnum);

        if (wallet != null) {
            if (StringUtils.isEmpty(wallet.getAddress())) {
                final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
                final WalletVo.WalletCreateInfo newAddress = simpleWalletRpcProvider.createWallet(userId);

                wallet.setUserId(userId);
                wallet.setCoinName(coinEnum);
                wallet.setAddress(newAddress.getAddress());
                wallet.setBankCode(newAddress.getBankCode());
                wallet.setBankName(newAddress.getBankName());
                wallet.setRecvCorpNm(newAddress.getRecvCorpNm());
                wallet.setTag(newAddress.getTag());

                //TODO 币币交易时 可能不需要此 处理 以后在判断
//                if (CoinEnum.USDT.equals(coinEnum) && StringUtils.isEmpty(wallet.getDepositDvcd())) {
//                    wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
//                }

                wallet.setUsingBalance(BigDecimal.ZERO);
                wallet.setAvailableBalance(BigDecimal.ZERO);
                wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
                wallet.setRegDt(LocalDateTime.now());
                walletDao.updateByUserIdAndCoinToAll(wallet);

                return WalletVo.ResCreateWallet.builder()
                        .address(wallet.getAddress())
                        .tag(wallet.getTag())
                        .build();
            }
            throw new ExchangeException(CodeEnum.WALLET_ALREADY_EXIST);
        }

        final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        WalletVo.WalletCreateInfo newAddress = simpleWalletRpcProvider.createWallet(userId);

        final Wallet newWallet = new Wallet();
        newWallet.setUserId(userId);
        newWallet.setCoinName(coinEnum);
        newWallet.setAddress(newAddress.getAddress());
        newWallet.setBankCode(newAddress.getBankCode());
        newWallet.setBankName(newAddress.getBankName());
        newWallet.setRecvCorpNm(newAddress.getRecvCorpNm());
        newWallet.setTag(newAddress.getTag());

        //TODO 币币交易时 可能不需要此 处理 以后在判断
//                if (CoinEnum.USDT.equals(coinEnum) && StringUtils.isEmpty(wallet.getDepositDvcd())) {
//                    wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
//                }

        newWallet.setUsingBalance(BigDecimal.ZERO);
        newWallet.setAvailableBalance(BigDecimal.ZERO);
        newWallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
        newWallet.setRegDt(LocalDateTime.now());
        walletDao.insert(newWallet);

        return WalletVo.ResCreateWallet.builder()
                .address(newWallet.getAddress())
                .tag(newWallet.getTag())
                .build();
    }

    @HardTransational
    public AdminWallet decreaseAvailableAdminBalance(final CoinEnum coinEnum, final WalletType walletType, final BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        final AdminWallet adminWallet = adminWalletDao.findOneByCoinAndTypeToAddressAndAvailableBalance(coinEnum, walletType);
        if (adminWallet == null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        adminWallet.setAvailableBalance(
                adminWallet
                        .getAvailableBalance()
                        .subtract(amount)
        );

        adminWalletDao.updateByCoinAndTypeToAvailableBalance(
                coinEnum,
                walletType,
                adminWallet.getAvailableBalance()
        );

        return adminWallet;
    }

    @HardTransational
    public AdminWallet increaseAvailableAdminBalance(final CoinEnum coinEnum, final WalletType walletType, final BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        final AdminWallet adminWallet = adminWalletDao.findOneByCoinAndTypeToAddressAndAvailableBalance(coinEnum, walletType);
        if (adminWallet == null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        adminWallet.setAvailableBalance(
                adminWallet
                        .getAvailableBalance()
                        .add(amount)
        );

        adminWalletDao.updateByCoinAndTypeToAvailableBalance(
                coinEnum,
                walletType,
                adminWallet.getAvailableBalance()
        );

        return adminWallet;
    }

    public BigDecimal getMinFee(final CoinEnum coinEnum) {
        final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.getMinFee();
    }

    public BigDecimal getRealWalletTotalBalance(final CoinEnum coinEnum, final Long userId) {
        final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.getRealBalance(userId);
    }

    @HardTransational
    public String sendFromHotWallet(final CoinEnum coinEnum, final String fromAddress, final String toAddress, final BigDecimal amount, final BigDecimal txFee)
            throws Exception {
        final SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.sendFromHotWallet(
                    fromAddress,
                    toAddress,
                    amount,
                    txFee
            );
    }

    @HardTransational
    public String sendTo(final CoinEnum coinEnum, final Long senderUserId, final String fromAddress, final String toAddress, final BigDecimal amount, final BigDecimal txFee)
            throws Exception {
        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.sendTo(senderUserId, fromAddress, toAddress, amount, txFee);
    }

}
