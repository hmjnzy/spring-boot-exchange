package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import lombok.Data;

@Data
public class TradeCoin {
    private CoinPairEnum coinPair;
    private int displayPriority;
    private ActiveEnum active;
}
