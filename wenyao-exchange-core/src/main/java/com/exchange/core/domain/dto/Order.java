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
public class Order implements Serializable {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal amountRemaining;
    private LocalDateTime completedDtm;
    private OrderType orderType;
    private BigDecimal price;
    private LocalDateTime regDtm;
    private OrderStatus status;
    private CoinPairEnum coinPair;
    private CoinEnum fromCoinName;
    private CoinEnum toCoinName;
    private LocalDateTime cancelDtm;


//    private String regDtText;
//    private String amountText;
//    private String amountRemainingText;
//    private String priceText;
//    private BigDecimal totalPrice;
//    private String totalPriceText;
//    public BigDecimal getTotalPrice() {
//        return amount.multiply(price).setScale(8, RoundingMode.DOWN);
//    }
//    public String getAmountText() {
//        return amount.toPlainString();
//    }
//    public String getAmountRemainingText() {
//        return amountRemaining.toPlainString();
//    }
//    public String getPriceText() {
//        return price.toPlainString();
//    }
//    public String getTotalPriceText() {
//        return getTotalPrice().setScale(8, RoundingMode.DOWN).toPlainString();
//    }
}
