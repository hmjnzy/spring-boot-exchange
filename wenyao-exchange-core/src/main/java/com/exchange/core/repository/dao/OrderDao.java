package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.enums.OrderStatus;
import com.exchange.core.domain.enums.OrderType;
import com.exchange.core.domain.vo.GroupOrderVo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao {
    void insert(Order order);
    List<Order> findAllByUserIdAndCoinPairAndStatus(Long userId, CoinPairEnum coinPair, OrderStatus status, Integer pageNo, Integer pageSize);
    List<Long> getTradeCandidateOrdersSell(CoinPairEnum coinPair, OrderType orderType, OrderStatus status,
                                           LocalDateTime regDtm, BigDecimal price, Integer pageNo, Integer pageSize);
    List<Long> getTradeCandidateOrdersBuy(CoinPairEnum coinPair, OrderType orderType, OrderStatus status,
                                           LocalDateTime regDtm, BigDecimal price, Integer pageNo, Integer pageSize);
    List<GroupOrderVo> getBuyingOrders(CoinPairEnum coinPair, OrderType orderType, OrderStatus status, Integer pageNo, Integer pageSize);
    List<GroupOrderVo> getSellingOrders(CoinPairEnum coinPair, OrderType orderType, OrderStatus status, Integer pageNo, Integer pageSize);
    Order findOneByIdAndUserIdAndStatus(Long orderId, Long userId, OrderStatus status);
    Order findOneByIdToAmountRemainingAndStatus(Long orderId);
    Order findOneByIdToUserIdAndAmountRemainingAndPriceAndStatus(Long orderId);
    int updateByIdToAmountRemainingAndCompletedDtmAndStatus(Order order);
    int updateByIdToCancelDtmAndStatus(Order order);
}
