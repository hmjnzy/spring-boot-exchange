package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.dto.Transaction;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.OrderStatus;
import com.exchange.core.domain.enums.OrderType;
import com.exchange.core.domain.enums.TradeStatusEnum;
import com.exchange.core.domain.vo.TradeStatusVo;
import com.exchange.core.provider.MqPublisher;
import com.exchange.core.util.CompareUtil;
import com.exchange.core.util.WalletUtil;
import com.github.pagehelper.PageInfo;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TradeService {

    private final OrderService orderService;
    private final WalletService walletService;
    private final MyHistoryOrderService myHistoryOrderService;
    private final MarketHistoryOrderService marketHistoryOrderService;
    private final MqPublisher mqPublisher;
    private final CoinService coinService;

    @Autowired
    public TradeService(OrderService orderService, WalletService walletService, MyHistoryOrderService myHistoryOrderService, MarketHistoryOrderService marketHistoryOrderService,
                        MqPublisher mqPublisher, CoinService coinService) {
        this.orderService = orderService;
        this.walletService = walletService;
        this.myHistoryOrderService = myHistoryOrderService;
        this.marketHistoryOrderService = marketHistoryOrderService;
        this.mqPublisher = mqPublisher;
        this.coinService = coinService;
    }

    @Builder
    private static class CalculatedOrdetStatusAndAmount {

        private OrderStatus targetOrderStatus;
        private BigDecimal targetReminingOrderAmount;
        private BigDecimal targetAmount;
        private BigDecimal targetPrice;

        private OrderStatus candidateOrderStatus;
        private BigDecimal candidateReminingOrderAmount;
        private BigDecimal candidateAmount;
        private BigDecimal candidatePrice;
    }

    private CalculatedOrdetStatusAndAmount getCalculatedOrdetStatusAndAmount(final Order targetOrder, final Order candidateOrder) {
        //自己此次卖出的 订单个数中 减去 他人等待交易买入的订单 eth个数
        final BigDecimal _compareAmount = targetOrder.getAmountRemaining().subtract(candidateOrder.getAmountRemaining());
        //_compareAmount -- 剩余的 我此次要卖出的 eth个数
        //(targetOrder == candidateOrder)
        if (CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.COMPLETED)//整好eth一一对应个数 我的订单 订单状态完成交易
                    .candidateOrderStatus(OrderStatus.COMPLETED)//对方等待买入订单状态也是 完成交易状态
                    .targetReminingOrderAmount(BigDecimal.ZERO)
                    .candidateReminingOrderAmount(BigDecimal.ZERO)
                    .targetAmount(targetOrder.getAmountRemaining())//此次 成交 eth个数
                    .targetPrice(candidateOrder.getPrice())//价格 最终按照 他人等待买入订单的价格
                    .candidateAmount(candidateOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        }
        //(targetOrder > candidateOrder)
        else if (CompareUtil.Condition.GT.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.PLACED)//未卖尽 我的订单还是 等待交易状态
                    .candidateOrderStatus(OrderStatus.COMPLETED)//对方等待买入订单状态也是 完成交易状态
                    .targetReminingOrderAmount(targetOrder.getAmountRemaining().subtract(candidateOrder.getAmountRemaining()))
                    .candidateReminingOrderAmount(BigDecimal.ZERO)
                    .targetAmount(candidateOrder.getAmountRemaining())//此次 成交 eth个数
                    .targetPrice(candidateOrder.getPrice())//价格 最终按照 他人等待买入订单的价格
                    .candidateAmount(candidateOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        }
        //(targetOrder < candidateOrder)
        else if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.COMPLETED)//整好eth一一对应个数 我的订单 订单状态完成交易
                    .candidateOrderStatus(OrderStatus.PLACED)//对方等待买入订单状态 为还是等待交易订单 - 因为 我为卖劲全部他所需要的
                    .targetReminingOrderAmount(BigDecimal.ZERO)
                    .candidateReminingOrderAmount(candidateOrder.getAmountRemaining().subtract(targetOrder.getAmountRemaining()))
                    .targetAmount(targetOrder.getAmountRemaining())//此次 成交 eth个数
                    .targetPrice(candidateOrder.getPrice())//价格 最终按照 他人等待买入订单的价格
                    .candidateAmount(targetOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        } else {
            return null;
        }
    }

    private boolean isAvailableTradeOrderStatus(OrderStatus targetOrderStatus, OrderStatus candidateOrderStatus) {
        if (OrderStatus.PLACED.equals(targetOrderStatus) && OrderStatus.PLACED.equals(candidateOrderStatus)) {
            return true;
        }
        return false;
    }

    @HardTransational
    public MyHistoryOrder tradePartial(final Order currentTargetOrder, final Long candidateOrderId,
                                       final BigDecimal fromCoinTradingFeePercent, final BigDecimal toCoinTradingFeePercent) {
        final Order targetOrder = orderService.findOneByIdToAmountRemainingAndStatus(currentTargetOrder.getId());
        if (!OrderStatus.PLACED.equals(targetOrder.getStatus())) {//查询当前 操作者挂载卖出订单的状态 如果不是等待状态就 不继续操作返回null
            return null;
        }

        final Order candidateOrder = orderService.findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus(candidateOrderId);
        if (!isAvailableTradeOrderStatus(targetOrder.getStatus(), candidateOrder.getStatus())) {
            return null;
        }

        /*
        * 成交价格按等待订单为准
        * */
        final CalculatedOrdetStatusAndAmount calculatedOrdetStatusAndAmount = this.getCalculatedOrdetStatusAndAmount(targetOrder, candidateOrder);
        if (calculatedOrdetStatusAndAmount == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        targetOrder.setId(currentTargetOrder.getId());
        targetOrder.setAmountRemaining(calculatedOrdetStatusAndAmount.targetReminingOrderAmount);
        targetOrder.setStatus(calculatedOrdetStatusAndAmount.targetOrderStatus);
        targetOrder.setCompletedDtm(now);
        orderService.updateByIdToAmountRemainingAndCompletedDtmAndStatus(targetOrder);
        //他人 等待买入订单 更新个数 以及订单状态
        candidateOrder.setId(candidateOrderId);
        candidateOrder.setAmountRemaining(calculatedOrdetStatusAndAmount.candidateReminingOrderAmount);
        candidateOrder.setStatus(calculatedOrdetStatusAndAmount.candidateOrderStatus);
        candidateOrder.setCompletedDtm(now);
        orderService.updateByIdToAmountRemainingAndCompletedDtmAndStatus(candidateOrder);

        targetOrder.setUserId(currentTargetOrder.getUserId());
        targetOrder.setOrderType(currentTargetOrder.getOrderType());
        targetOrder.setCoinPair(currentTargetOrder.getCoinPair());
        targetOrder.setFromCoinName(currentTargetOrder.getFromCoinName());
        targetOrder.setToCoinName(currentTargetOrder.getToCoinName());

        candidateOrder.setCoinPair(currentTargetOrder.getCoinPair());
        candidateOrder.setFromCoinName(currentTargetOrder.getToCoinName());
        candidateOrder.setToCoinName(currentTargetOrder.getFromCoinName());

        BigDecimal targetUsingBalance;
        BigDecimal candidateUsingBalance;
        if (targetOrder.getOrderType().equals(OrderType.SELL)) {
            /*
            * 此次操作者为卖
            * */
            targetUsingBalance = calculatedOrdetStatusAndAmount.targetAmount;//将要卖出的ETH个数
            candidateUsingBalance = calculatedOrdetStatusAndAmount.candidateAmount.multiply(calculatedOrdetStatusAndAmount.candidatePrice);
            candidateOrder.setOrderType(OrderType.BUY);
        } else {
            /*
            * 此次操作者为买
            * */
            targetUsingBalance = calculatedOrdetStatusAndAmount.targetAmount.multiply(currentTargetOrder.getPrice());
            candidateUsingBalance = calculatedOrdetStatusAndAmount.candidateAmount;
            candidateOrder.setOrderType(OrderType.SELL);
        }

        /*
        * 此次操作者为卖时 减去自己的 此次将要卖出的ETH个数
        * */
        walletService.decreaseUsingBalance(
                targetOrder.getUserId(),
                targetOrder.getFromCoinName(),
                WalletUtil.scale(targetUsingBalance)
        );

        /*
        * 此次操作者为卖时 减去等待买入者的 此次将要花的USDT {ETH个数 * 单价(USDT) = 买入总金额(USDT)}
        * */
        walletService.decreaseUsingBalance(
                candidateOrder.getUserId(),
                candidateOrder.getFromCoinName(),
                WalletUtil.scale(candidateUsingBalance)
        );

        /*
        * MyHistoryOrder 不管买卖 插入两次
        * 1.++指定交易币种 扣除费率后的 个数
        * 2.费率++到管理员冷(COLD)钱包内
        * */
        final MyHistoryOrder myHistoryOrder = walletService.increaseAvailableBalanceAndFeeBalance(
                targetOrder,//操作者订单
                candidateOrder,//等待订单
                targetOrder.getToCoinName(),//卖出时USDT
                toCoinTradingFeePercent,//卖出时交易费率为USDT
                calculatedOrdetStatusAndAmount.targetAmount,
                calculatedOrdetStatusAndAmount.targetPrice
        );
        myHistoryOrderService.insert(myHistoryOrder);
        final MyHistoryOrder candidateHistoryOrder = walletService.increaseAvailableBalanceAndFeeBalance(
                candidateOrder,//等待订单
                targetOrder,//操作者订单
                candidateOrder.getToCoinName(),//卖出时ETH
                fromCoinTradingFeePercent,//卖出时交易费率为ETH
                calculatedOrdetStatusAndAmount.candidateAmount,
                calculatedOrdetStatusAndAmount.candidatePrice
        );
        myHistoryOrderService.insert(candidateHistoryOrder);

        //OrderType按交易请求者插入 例 请求者SEll MarketHistoryOrder.OrderType=SEll
        marketHistoryOrderService.registMarketHistory(
                targetOrder,
                calculatedOrdetStatusAndAmount.targetAmount,
                calculatedOrdetStatusAndAmount.targetPrice,
                candidateOrder,
                calculatedOrdetStatusAndAmount.candidateAmount,
                calculatedOrdetStatusAndAmount.candidatePrice
        );

        return myHistoryOrder;
    }

    @HardTransational
    public TradeStatusVo trade(final Order targetOrder, final BigDecimal fromCoinTradingFeePercent, final BigDecimal toCoinTradingFeePercent) {
        /*
        * 挂单同时 查询条件匹配的等待买卖单前500个
        * */
        List<Long> candidateOrderIds = orderService.getTradeCandidateOrders(targetOrder, 1);
        PageInfo<Long> candidateOrderIdsPageInfo =  new PageInfo<>(candidateOrderIds);
        /*
        * 无匹配时 等待处理
        * */
        if (candidateOrderIdsPageInfo.getSize() == 0) {
            return TradeStatusVo.builder()
                    .tradeStatusEnum(TradeStatusEnum.SHOULD_TRADED_BATCH_TRADED)
                    .order(targetOrder)
                    .build();
        }
        //已成功交易订单按价格合并一起
        final Map<String, MyHistoryOrder> tradedOrdersMap = new HashMap<>();
        final int totalPage = candidateOrderIdsPageInfo.getPages();
        boolean bFinish = false;
        for (int pageNo = 1 ; pageNo <= totalPage ; pageNo++) {
            if (bFinish) {
                break;
            }

            if (pageNo > 1) {
                candidateOrderIds = orderService.getTradeCandidateOrders(targetOrder, 1);//之前等待订单已处理所以还是从分页1开始
                candidateOrderIdsPageInfo =  new PageInfo<>(candidateOrderIds);
                if (candidateOrderIdsPageInfo.getSize() == 0) {
                    break;
                }
            }
            //交易请求价位匹配的等待订单编号遍历
            final Iterator<Long> candidateOrderId = candidateOrderIdsPageInfo.getList().iterator();
            while (candidateOrderId.hasNext()) {
                //直接处理交易逻辑
                final MyHistoryOrder myHistoryOrder = this.tradePartial(targetOrder, candidateOrderId.next(), fromCoinTradingFeePercent, toCoinTradingFeePercent);
                if (myHistoryOrder == null) {
                    bFinish = true;
                    break;
                }
                //注: 到这部分处理情况, ETH买卖个数 未满 或 整好。不会超出
                final String key = myHistoryOrder.getPrice().toPlainString();
                if (tradedOrdersMap.containsKey(key)) {//之前已有相同单价为Key的存储
                    MyHistoryOrder _myHistoryOrder = tradedOrdersMap.get(key);
                    _myHistoryOrder.setAmount(_myHistoryOrder.getAmount().add(myHistoryOrder.getAmount()));//之前已加的ETH买卖个数
                    tradedOrdersMap.put(key, _myHistoryOrder);
                } else {//单价当Key存储
                    tradedOrdersMap.put(key, myHistoryOrder);
                }
            }
        }

//        //成功交易发布消息（买卖等待订单列表，已交易订单列表）
//        mqPublisher.websockMessagePublish(
//                MessagePacket.builder()
//                        .cmd(CommandEnum.TRADE)
//                        .scope(PacketScopeEnum.PUBLIC)
//                        .userId(order.getUserId())
//                        .build()
//        );

        return TradeStatusVo.builder()
                .tradeStatusEnum(TradeStatusEnum.SHOULD_TRADED_PARTIAL)
                .order(targetOrder)
                .tradedOrders(
                        tradedOrdersMap.isEmpty() ?
                                new ArrayList<>() : new ArrayList(tradedOrdersMap.values())//已成功交易的订单
                )
                .build();
    }

}
