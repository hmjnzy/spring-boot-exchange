package com.exchange.core.service;

import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.Chart;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.ChartVo;
import com.exchange.core.repository.dao.ChartDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChartService {

    private final ChartDao chartDao;

    @Autowired
    public ChartService(ChartDao chartDao) {
        this.chartDao = chartDao;
    }

    @SoftTransational
    public void insert(final Chart chart) {
        this.chartDao.insert(chart);
    }

    public int findByDtAndCoinPairToCount(final Long dt, final CoinPairEnum coinPair) {
        return this.chartDao.findByDtAndCoinPairToCount(dt, coinPair);
    }

    public List<List<String>> getChartData(final CoinPairEnum coinPair) {
        final List<ChartVo> data =  this.chartDao.getChartData(coinPair);
        List<List<String>> charts = new ArrayList<>();
        for (ChartVo chartVo : data) {
            charts.add(chartVo.getRow());
        }

        return charts;
    }

}
