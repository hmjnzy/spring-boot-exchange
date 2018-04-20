package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.*;
import com.exchange.core.domain.vo.GroupOrderVo;
import com.exchange.core.domain.vo.OrderVo;
import com.exchange.core.exception.ExchangeException;
import com.exchange.core.repository.dao.OrderDao;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Order findOneByIdAndUserIdAndStatus(final Long orderId, final Long userId, final OrderStatus status) {
        return this.orderDao.findOneByIdAndUserIdAndStatus(orderId, userId, status);
    }

    public Order findOneByIdToAmountRemainingAndStatus(final Long orderId) {
        return this.orderDao.findOneByIdToAmountRemainingAndStatus(orderId);
    }

    public Order findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus(final Long orderId) {
        return this.orderDao.findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus(orderId);
    }

    public int updateByIdToAmountRemainingAndCompletedDtmAndStatus(final Order order) {
        return this.orderDao.updateByIdToAmountRemainingAndCompletedDtmAndStatus(order);
    }

    public int updateByIdToCancelDtmAndStatus(final Order order) {
        return this.orderDao.updateByIdToCancelDtmAndStatus(order);
    }

    /*
    * 挂单同时 查询条件匹配的等待买卖单
    * */
    public List<Long> getTradeCandidateOrders(final Order order, final Integer pageNo) {
        if (OrderType.BUY.equals(order.getOrderType())) {

            return this.orderDao.getTradeCandidateOrdersSell(
                    order.getCoinPair(),
                    OrderType.SELL,
                    OrderStatus.PLACED,
                    LocalDateTime.now().minusDays(365L),
                    order.getPrice(),
                    pageNo, 20);
        } else if (OrderType.SELL.equals(order.getOrderType())) {

            return this.orderDao.getTradeCandidateOrdersBuy(
                    order.getCoinPair(),
                    OrderType.BUY,
                    OrderStatus.PLACED,
                    LocalDateTime.now().minusDays(365L),
                    order.getPrice(),
                    pageNo, 20);
        } else {
            throw new ExchangeException(CodeEnum.INVALID_ORDER_TYPE);
        }
    }

    @HardTransational
    public Order buy(final Long userId, final BigDecimal price, final BigDecimal amount, final CoinPairEnum coinPair, final CoinEnum fromCoin, final CoinEnum toCoin) {
        final Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setAmountRemaining(amount);
        order.setCompletedDtm(null);
        order.setOrderType(OrderType.BUY);
        order.setPrice(price);
        order.setRegDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setCoinPair(coinPair);
        order.setFromCoinName(fromCoin);
        order.setToCoinName(toCoin);
        order.setCancelDtm(null);
        this.orderDao.insert(order);
        return order;
    }

    @HardTransational
    public Order sell(final Long userId, final BigDecimal price, final BigDecimal amount, final CoinPairEnum coinPair, final CoinEnum fromCoin, final CoinEnum toCoin) {
        final Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setAmountRemaining(amount);
        order.setCompletedDtm(null);
        order.setOrderType(OrderType.SELL);
        order.setPrice(price);
        order.setRegDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setCoinPair(coinPair);
        order.setFromCoinName(fromCoin);
        order.setToCoinName(toCoin);
        order.setCancelDtm(null);
        this.orderDao.insert(order);
        return order;
    }

    public OrderVo.ResRealTimeOrders getRealTimeOrders(final OrderVo.ReqRealTimeOrders request) {

        List<GroupOrderVo> order;
        if (request.getOrderType().equals(OrderType.SELL)) {
            order = this.orderDao.getSellingOrders(
                    request.getCoinPair(),
                    request.getOrderType(),
                    OrderStatus.PLACED,
                    request.getPageNo(),
                    20
            );
        } else {
            order = this.orderDao.getBuyingOrders(
                    request.getCoinPair(),
                    request.getOrderType(),
                    OrderStatus.PLACED,
                    request.getPageNo(),
                    20
            );
        }

        final PageInfo<GroupOrderVo> orderPageInfo = new PageInfo<>(order);

        return OrderVo.ResRealTimeOrders.builder()
                .pageNum(orderPageInfo.getPageNum())
                .pageSize(orderPageInfo.getPageSize())
                .size(orderPageInfo.getSize())
                .startRow(orderPageInfo.getStartRow())
                .endRow(orderPageInfo.getEndRow())
                .total(orderPageInfo.getTotal())
                .pages(orderPageInfo.getPages())
                .list(orderPageInfo.getList())
                .build();
    }

    public OrderVo.ResMyOrders getMyOrders(final Long userId, final OrderVo.ReqMyOrders request) {
        final List<Order> order = orderDao.findAllByUserIdAndCoinPairAndStatus(
                userId,
                request.getCoinPair(),
                OrderStatus.PLACED,
                request.getPageNo(),
                20
        );
        final PageInfo<Order> orderPageInfo = new PageInfo<>(order);
        return OrderVo.ResMyOrders.builder()
                .pageNum(orderPageInfo.getPageNum())
                .pageSize(orderPageInfo.getPageSize())
                .size(orderPageInfo.getSize())
                .startRow(orderPageInfo.getStartRow())
                .endRow(orderPageInfo.getEndRow())
                .total(orderPageInfo.getTotal())
                .pages(orderPageInfo.getPages())
                .list(orderPageInfo.getList())
                .build();
    }

}
