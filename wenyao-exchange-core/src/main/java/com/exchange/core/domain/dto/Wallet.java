package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CoinEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Wallet {
    private Long id;
    private Long userId;
    private CoinEnum coinName;
    private String address;
    private String bankCode;
    private String bankName;
    private String recvCorpNm;
    private String tag;
    private String depositDvcd;
    private BigDecimal usingBalance;
    private BigDecimal availableBalance;
    private BigDecimal todayWithdrawalTotalBalance;
    private LocalDateTime regDt;
    //private String realBalance;
}
