package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.MarketHistoryOrder;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.OrderStatus;
import com.exchange.core.domain.vo.OrderVo;
import com.exchange.core.repository.dao.MarketHistoryOrderDao;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketHistoryOrderService {

    private final MarketHistoryOrderDao marketHistoryOrderDao;

    @Autowired
    public MarketHistoryOrderService(MarketHistoryOrderDao marketHistoryOrderDao) {
        this.marketHistoryOrderDao = marketHistoryOrderDao;
    }

    @SoftTransational
    public void insert(final MarketHistoryOrder marketHistoryOrder) {
        //marketHistoryOrder.setRegDtm(LocalDateTime.now());
        marketHistoryOrder.setStatus(OrderStatus.COMPLETED);
        this.marketHistoryOrderDao.insert(marketHistoryOrder);
    }

    @HardTransational
    public void registMarketHistory(final Order targetOrder, final BigDecimal targetAmount, final BigDecimal targetPrice,
                                                  final Order candidateOrder, final BigDecimal candidateAmount, final BigDecimal candidatePrice) {
        final MarketHistoryOrder marketHistoryOrder = new MarketHistoryOrder();
        if (targetOrder.getId() > candidateOrder.getId()) {
            //如卖的 等待订单 先生成的 情况
            marketHistoryOrder.setUserId(targetOrder.getUserId());
            marketHistoryOrder.setOrderId(targetOrder.getId());
            marketHistoryOrder.setPrice(targetPrice);
            marketHistoryOrder.setAmount(targetAmount);
            marketHistoryOrder.setOrderType(targetOrder.getOrderType());
            marketHistoryOrder.setCoinPair(targetOrder.getCoinPair());
            marketHistoryOrder.setToCoinName(targetOrder.getToCoinName());//eth
            marketHistoryOrder.setFromCoinName(targetOrder.getFromCoinName());//usdt
        } else {
            //如买入的 等待订单 有的情况 此次操作卖时
            marketHistoryOrder.setUserId(candidateOrder.getUserId());//卖出操作时: 等待买入订单的用户编号
            marketHistoryOrder.setOrderId(candidateOrder.getId());//卖出操作时: 等待买入订单的编号
            marketHistoryOrder.setPrice(candidatePrice);//单价
            marketHistoryOrder.setAmount(candidateAmount);//等待买入人的 买入eth个数
            marketHistoryOrder.setOrderType(candidateOrder.getOrderType());//BUY
            marketHistoryOrder.setCoinPair(candidateOrder.getCoinPair());
            marketHistoryOrder.setToCoinName(candidateOrder.getToCoinName());//eth
            marketHistoryOrder.setFromCoinName(candidateOrder.getFromCoinName());//usdt
        }
        marketHistoryOrder.setCompletedDtm(LocalDateTime.now());
        //marketHistoryOrder.setDt(DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(LocalDateTime.now()));
        //marketHistoryOrder.setRegDtm(LocalDateTime.now());
        marketHistoryOrder.setStatus(OrderStatus.COMPLETED);
        this.marketHistoryOrderDao.insert(marketHistoryOrder);
    }

    public OrderVo.ResMarketHistoryOrders getMarketHistoryOrders(final OrderVo.ReqMarketHistoryOrders request) {
        final List<MarketHistoryOrder> marketHistoryOrder = this.marketHistoryOrderDao.findAllByCoinPairOrderById(
                request.getCoinPair(),
                request.getPageNo(),
                20
        );

        final PageInfo<MarketHistoryOrder> marketHistoryOrderPageInfo = new PageInfo<>(marketHistoryOrder);

        return OrderVo.ResMarketHistoryOrders.builder()
                .pageNum(marketHistoryOrderPageInfo.getPageNum())
                .pageSize(marketHistoryOrderPageInfo.getPageSize())
                .size(marketHistoryOrderPageInfo.getSize())
                .startRow(marketHistoryOrderPageInfo.getStartRow())
                .endRow(marketHistoryOrderPageInfo.getEndRow())
                .total(marketHistoryOrderPageInfo.getTotal())
                .pages(marketHistoryOrderPageInfo.getPages())
                .list(marketHistoryOrderPageInfo.getList())
                .build();
    }

}
