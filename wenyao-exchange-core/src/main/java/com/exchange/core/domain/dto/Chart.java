package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.enums.CrawlEnum;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class Chart {

    private Long dt;
    private CoinPairEnum coinPair;
    //private BigDecimal price;
    //private BigDecimal adjClose;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private CrawlEnum crawlFrom;

    //时间查询条件字段
    private Long startTime;
    private Long endTime;


}
