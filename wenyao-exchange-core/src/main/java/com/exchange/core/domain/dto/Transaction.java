package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String id;
    private Long userId;
    private CoinEnum coinName;
    private CategoryEnum category;
    private String txId;
    private String address;
    private String tag;
    private String bankNm;
    private String recvNm;
    private BigDecimal amount;
    private LocalDateTime regDt;
    private LocalDateTime completeDtm;
    private BigInteger confirmation;
    private StatusEnum status;
    private String reason;
    private BigDecimal withdrawalFeeAmount;
}
