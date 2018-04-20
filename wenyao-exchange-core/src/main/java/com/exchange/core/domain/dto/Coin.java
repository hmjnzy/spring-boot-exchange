package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Coin {
    private CoinEnum name;
    private String fullName;
    private int displayPriority;
    private String rpcUrl;
    private String logoUrl;
    private LocalDateTime regDtm;
    private ActiveEnum active;
    private BigDecimal withdrawalMinAmount;
    private BigDecimal withdrawalAutoAllowMaxAmount;
    private BigDecimal withdrawalFeeAmount;
    private BigDecimal autoCollectMinAmount;
    private BigDecimal tradingFeePercent;
    private BigDecimal tradingMinAmount;
    private BigDecimal marginTradingFeePercent;
    private int depositScanPageOffset;
    private int depositScanPageSize;
    private BigInteger depositAllowConfirmation;
}