package com.exchange.core.domain.vo;

import com.exchange.core.domain.dto.MarketHistoryOrder;
import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.enums.OrderType;
import lombok.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OrderVo {

    @Data
    public static class ReqOrder {
        @NotNull
        @DecimalMin("0.00000001")
        private BigDecimal price;
        private OrderType orderType;
        @NotNull
        @DecimalMin("0.00000001")
        private BigDecimal amount;
        @NotNull
        private CoinPairEnum coinPair;
        @NotNull
        private CoinEnum fromCoin;
        @NotNull
        private CoinEnum toCoin;
    }

    @Getter
    @Setter
    @Builder
    public static class ResCancel {
        private Order order;
    }

    @Getter
    @Setter
    @Builder
    public static class ReqCancel {
        @NotNull
        private Long orderId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqRealTimeOrders {
        @NotNull
        private OrderType orderType;
        @NotNull
        private CoinPairEnum coinPair;
        private Integer pageNo;
        //Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    public static class ResRealTimeOrders {
        private Integer pageNum;
        private Integer pageSize;
        private Integer size;
        private Integer startRow;
        private Integer endRow;
        private Long total;
        private Integer pages;
        private List<GroupOrderVo> list;
    }

    @Data
    public static class ReqMyOrders {
        @NotNull
        private CoinPairEnum coinPair;
        private Integer pageNo;
        //Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    public static class ResMyOrders {
        private Integer pageNum;
        private Integer pageSize;
        private Integer size;
        private Integer startRow;
        private Integer endRow;
        private Long total;
        private Integer pages;
        private List<Order> list;
    }

    @Data
    public static class ReqMarketHistoryOrders {
        @NotNull
        private CoinPairEnum coinPair;
        private Integer pageNo;
        //Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    public static class ResMarketHistoryOrders {
        private Integer pageNum;
        private Integer pageSize;
        private Integer size;
        private Integer startRow;
        private Integer endRow;
        private Long total;
        private Integer pages;
        private List<MarketHistoryOrder> list;
    }

    @Data
    public static class ReqMyHistoryOrders {
        @NotNull
        private CoinPairEnum coinPair;
        private Integer pageNo;
        //Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    public static class ResMyHistoryOrders {
        private Integer pageNum;
        private Integer pageSize;
        private Integer size;
        private Integer startRow;
        private Integer endRow;
        private Long total;
        private Integer pages;
        private List<MyHistoryOrder> list;
    }

}
