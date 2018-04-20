package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ManualTransaction {
    private String id;
    private Long userId;
    private CoinEnum coinName;
    private CategoryEnum category;
    private String address;
    private String tag;
    private String bankNm;
    private String recvNm;
    private String depositDvcd;
    private BigDecimal amount;
    private LocalDateTime regDt;
    private StatusEnum status;
    private String reason;
    private BigDecimal withdrawalFeeAmount;
    //Google Auth
    private int authCode;
}
