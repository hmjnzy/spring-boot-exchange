package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.enums.OrderStatus;
import com.exchange.core.domain.enums.OrderType;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MyHistoryOrder implements Serializable {
    private Long id;
    private Long userId;
    private Long orderId;
    private BigDecimal amount;
    private LocalDateTime completedDtm;
    private String dt;
    private OrderType orderType;
    private BigDecimal price;
    private LocalDateTime regDtm;
    private OrderStatus status;
    private CoinPairEnum coinPair;
    private CoinEnum fromCoinName;
    private CoinEnum toCoinName;
    private Long toUserId;
    private Long toOrderId;
//    private BigDecimal totalPrice;
//    public BigDecimal getTotalPrice() {
//        return amount.multiply(price);
//    }
}
