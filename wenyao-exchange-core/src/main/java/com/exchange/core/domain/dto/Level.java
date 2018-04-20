package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.LevelEnum;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class Level {

    private CoinEnum coinName;
    private LevelEnum level;
    private BigDecimal onceAmount;
    private BigDecimal onedayAmount;
}
