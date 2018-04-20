package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminWallet {
    private CoinEnum coinName;
    private WalletType type;
    private String address;
    private String tag;
    private String recvCorpNm;
    private String bankName;
    private String bankCode;
    private BigDecimal availableBalance;
    private BigDecimal usingBalance;
    private LocalDateTime regDt;
}
