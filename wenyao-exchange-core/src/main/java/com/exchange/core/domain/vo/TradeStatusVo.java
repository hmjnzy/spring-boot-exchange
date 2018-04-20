package com.exchange.core.domain.vo;

import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.dto.Order;
import com.exchange.core.domain.enums.TradeStatusEnum;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TradeStatusVo {
    private TradeStatusEnum tradeStatusEnum;
    private Order order;
    private List<MyHistoryOrder> tradedOrders;
}
