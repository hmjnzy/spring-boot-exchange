package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.enums.OrderStatus;
import com.exchange.core.domain.enums.OrderType;
import com.exchange.core.domain.vo.GroupOrderVo;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.OrderDao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderDaoImpl extends BaseDao implements OrderDao {

    @Override
    public void insert(final Order order) {
        super.getSqlSession().insert(super.getStatement("insert"), order);
    }

    @Override
    public List<Order> findAllByUserIdAndCoinPairAndStatus(final Long userId, final CoinPairEnum coinPair, final OrderStatus status,
                                                           final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final Order order = new Order();
        order.setUserId(userId);
        order.setCoinPair(coinPair);
        order.setStatus(status);
        return super.getSqlSession().selectList(super.getStatement("findAllByUserIdAndCoinPairAndStatus"), order);
    }

    @Override
    public List<Long> getTradeCandidateOrdersSell(final CoinPairEnum coinPair, final OrderType orderType, final OrderStatus status,
                                                  final LocalDateTime regDtm, final BigDecimal price, final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final Order order = new Order();
        order.setCoinPair(coinPair);
        order.setOrderType(orderType);
        order.setStatus(status);
        order.setRegDtm(regDtm);
        order.setPrice(price);
        return super.getSqlSession().selectList(super.getStatement("getTradeCandidateOrdersSell"), order);
    }

    @Override
    public List<Long> getTradeCandidateOrdersBuy(final CoinPairEnum coinPair, final OrderType orderType, final OrderStatus status,
                                                 final LocalDateTime regDtm, final BigDecimal price, final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final Order order = new Order();
        order.setCoinPair(coinPair);
        order.setOrderType(orderType);
        order.setStatus(status);
        order.setRegDtm(regDtm);
        order.setPrice(price);
        return super.getSqlSession().selectList(super.getStatement("getTradeCandidateOrdersBuy"), order);
    }

    @Override
    public List<GroupOrderVo> getBuyingOrders(final CoinPairEnum coinPair, final OrderType orderType, final OrderStatus status,
                                              final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final Order order = new Order();
        order.setCoinPair(coinPair);
        order.setOrderType(orderType);
        order.setStatus(status);
        return super.getSqlSession().selectList(super.getStatement("getBuyingOrders"), order);
    }

    @Override
    public List<GroupOrderVo> getSellingOrders(final CoinPairEnum coinPair, final OrderType orderType, final OrderStatus status,
                                               final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final Order order = new Order();
        order.setCoinPair(coinPair);
        order.setOrderType(orderType);
        order.setStatus(status);
        return super.getSqlSession().selectList(super.getStatement("getSellingOrders"), order);
    }

    @Override
    public Order findOneByIdAndUserIdAndStatus(final Long orderId, final Long userId, final OrderStatus status) {
        final Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(status);
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdAndUserIdAndStatus"), order);
    }

    @Override
    public Order findOneByIdToAmountRemainingAndStatus(final Long orderId) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdToAmountRemainingAndStatus"), orderId);
    }

    @Override
    public Order findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus(final Long orderId) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus"), orderId);
    }

    @Override
    public int updateByIdToAmountRemainingAndCompletedDtmAndStatus(final Order order) {
        return super.getSqlSession().update(super.getStatement("updateByIdToAmountRemainingAndCompletedDtmAndStatus"), order);
    }

    @Override
    public int updateByIdToCancelDtmAndStatus(final Order order) {
        return super.getSqlSession().update(super.getStatement("updateByIdToCancelDtmAndStatus"), order);
    }

}
