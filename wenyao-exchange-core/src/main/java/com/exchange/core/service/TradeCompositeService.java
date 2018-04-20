package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.*;
import com.exchange.core.domain.vo.OrderVo;
import com.exchange.core.domain.vo.TradeStatusVo;
import com.exchange.core.exception.ExchangeException;
import com.exchange.core.util.CompareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TradeCompositeService {

    private final TradeService tradeService;
    private final TradeCoinService tradeCoinService;
    private final OrderService orderService;
    private final WalletService walletService;
    private final CoinService coinService;

    @Autowired
    public TradeCompositeService(TradeService tradeService, TradeCoinService tradeCoinService, OrderService orderService, WalletService walletService, CoinService coinService) {
        this.tradeService = tradeService;
        this.tradeCoinService = tradeCoinService;
        this.orderService = orderService;
        this.walletService = walletService;
        this.coinService = coinService;
    }

    @HardTransational
    public TradeStatusVo buy(final Long userId, final OrderVo.ReqOrder request) {
        if (!OrderType.BUY.equals(request.getOrderType())) {
            throw new ExchangeException(CodeEnum.INVAILD_ORDER_TYPE);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getPrice())) ||
                CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(request.getPrice()))
                ) {
            throw new ExchangeException(CodeEnum.PRICE_CANT_ZERO_OR_UNDER_ZERO);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getAmount())) ||
                CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(request.getAmount()))
                ) {
            throw new ExchangeException(CodeEnum.AMOUNT_CANT_ZERO_OR_UNDER_ZERO);
        }

        if (!CoinPairEnum.contains(request.getFromCoin().name() + "_" + request.getToCoin().name())) {
            throw new ExchangeException(CodeEnum.COINPAIR_NOT_EXIST);
        }

        final TradeCoin coinPair = tradeCoinService.findByCoinPairToActive(request.getCoinPair());
        if (coinPair == null || ActiveEnum.N.equals(coinPair.getActive())) {
            throw new ExchangeException(CodeEnum.COINPAIR_NOT_ACTIVE);
        }

        final Coin fromCoin = coinService.findOneByNameToTradingFeePercentAndTradingMinAmount(request.getFromCoin(), ActiveEnum.Y);//USDT
        if (fromCoin == null) {
            throw new ExchangeException(CodeEnum.COIN_NOT_ACTIVE);
        }

        final Coin toCoin = coinService.findOneByNameToTradingFeePercentAndTradingMinAmount(request.getToCoin(), ActiveEnum.Y);//ETH
        if (toCoin == null) {
            throw new ExchangeException(CodeEnum.COIN_NOT_ACTIVE);
        }

        //单价(USDT)*购买总数(ETH) = 总价(USDT)(未扣除费率)
        final BigDecimal baseCoinTotalPrice = request.getPrice().multiply(request.getAmount());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(baseCoinTotalPrice, fromCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(request.getAmount(), toCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        //同步判断USDT余额
        final Wallet fromCoinWallet = walletService.findOneByUserIdAndCoinToBalance(userId, request.getFromCoin());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(fromCoinWallet.getAvailableBalance(), baseCoinTotalPrice))) {
            throw new ExchangeException(CodeEnum.COIN_INSUFFICIENT_BALANCE);
        }

        //减USDT余额
        fromCoinWallet.setAvailableBalance(fromCoinWallet.getAvailableBalance().subtract(baseCoinTotalPrice));
        fromCoinWallet.setUsingBalance(fromCoinWallet.getUsingBalance().add(baseCoinTotalPrice));
        walletService.updateByIdToAllBalance(fromCoinWallet);

        final Order order = orderService.buy(userId, request.getPrice(), request.getAmount(), request.getCoinPair(), request.getFromCoin(), request.getToCoin());

        return tradeService.trade(order, fromCoin.getTradingFeePercent(), toCoin.getTradingFeePercent());
    }

    @HardTransational
    public TradeStatusVo sell(final Long userId, final OrderVo.ReqOrder request) {
        if (!OrderType.SELL.equals(request.getOrderType())) {
            throw new ExchangeException(CodeEnum.INVAILD_ORDER_TYPE);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getPrice())) ||
                CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(request.getPrice()))
        ) {
            throw new ExchangeException(CodeEnum.PRICE_CANT_ZERO_OR_UNDER_ZERO);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getAmount())) ||
                CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(request.getAmount()))
        ) {
            throw new ExchangeException(CodeEnum.AMOUNT_CANT_ZERO_OR_UNDER_ZERO);
        }

        if (!CoinPairEnum.contains(request.getToCoin().name() + "_" + request.getFromCoin().name())) {
            throw new ExchangeException(CodeEnum.COINPAIR_NOT_EXIST);
        }

        final TradeCoin coinPair = tradeCoinService.findByCoinPairToActive(request.getCoinPair());
        if (coinPair == null || ActiveEnum.N.equals(coinPair.getActive())) {
            throw new ExchangeException(CodeEnum.COINPAIR_NOT_ACTIVE);
        }

        final Coin fromCoin = coinService.findOneByNameToTradingFeePercentAndTradingMinAmount(request.getFromCoin(), ActiveEnum.Y);//ETH
        if (fromCoin == null) {
            throw new ExchangeException(CodeEnum.COIN_NOT_ACTIVE);
        }

        final Coin toCoin = coinService.findOneByNameToTradingFeePercentAndTradingMinAmount(request.getToCoin(), ActiveEnum.Y);//USDT
        if (toCoin == null) {
            throw new ExchangeException(CodeEnum.COIN_NOT_ACTIVE);
        }

        //单价(USDT)*购买总数(ETH) = 总价(USDT)(未扣除费率)
        final BigDecimal baseCoinTotalPrice = request.getPrice().multiply(request.getAmount());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(baseCoinTotalPrice, toCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(request.getAmount(), fromCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        //同步判断ETH余额
        final Wallet fromCoinWallet = walletService.findOneByUserIdAndCoinToBalance(userId, request.getFromCoin());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(fromCoinWallet.getAvailableBalance(), request.getAmount()))) {
            throw new ExchangeException(CodeEnum.COIN_INSUFFICIENT_BALANCE);
        }

        //减ETH余额
        fromCoinWallet.setAvailableBalance(fromCoinWallet.getAvailableBalance().subtract(request.getAmount()));
        fromCoinWallet.setUsingBalance(fromCoinWallet.getUsingBalance().add(request.getAmount()));
        walletService.updateByIdToAllBalance(fromCoinWallet);

        final Order order = orderService.sell(userId, request.getPrice(), request.getAmount(), request.getCoinPair(), request.getFromCoin(), request.getToCoin());

        return tradeService.trade(order, fromCoin.getTradingFeePercent(), toCoin.getTradingFeePercent());
    }

    @HardTransational
    public OrderVo.ResCancel cancel(final Long userId, final Long orderId) {
        final Order order = orderService.findOneByIdAndUserIdAndStatus(
                orderId,
                userId,
                OrderStatus.PLACED
        );
        if (order == null) {
            throw new ExchangeException(CodeEnum.ORDER_CANCEL_FAIL);
        }

        order.setId(orderId);
        order.setCancelDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCEL);
        orderService.updateByIdToCancelDtmAndStatus(order);

        if (OrderType.BUY.equals(order.getOrderType())) {
            final BigDecimal canceledBalance = order.getAmountRemaining().multiply(order.getPrice()).setScale(8);

            walletService.cancelBalance(
                    userId,
                    order.getFromCoinName(),
                    canceledBalance
            );
        } else if (OrderType.SELL.equals(order.getOrderType())) {

            walletService.cancelBalance(
                    userId,
                    order.getFromCoinName(),
                    order.getAmountRemaining()
            );
        } else {
            throw new ExchangeException(CodeEnum.INVALID_ORDER_TYPE);
        }

        return OrderVo.ResCancel.builder()
                .order(order)
                .build();
    }

}
