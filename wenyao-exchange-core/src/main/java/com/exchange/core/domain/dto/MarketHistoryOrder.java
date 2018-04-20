package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MarketHistoryOrder {
    private Long id;
    private Long userId;
    private Long orderId;
    private BigDecimal price;
    private BigDecimal amount;
    //private Long dt;
    private OrderType orderType;
    private LocalDateTime completedDtm;
    //private LocalDateTime regDtm;
    private OrderStatus status;
    private CoinPairEnum coinPair;
    private CoinEnum fromCoinName;
    private CoinEnum toCoinName;
}
