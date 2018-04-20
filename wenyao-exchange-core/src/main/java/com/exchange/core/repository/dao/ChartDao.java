package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Chart;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.ChartVo;
import java.util.List;

public interface ChartDao {
    int insert(Chart chart);
    int findByDtAndCoinPairToCount(Long dt, CoinPairEnum coinPair);
    List<ChartVo> getChartData(CoinPairEnum coinPair);
}
