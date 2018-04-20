package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Chart;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.ChartVo;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.ChartDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ChartDaoImpl extends BaseDao implements ChartDao {

    @Override
    public int insert(final Chart chart) {
        return super.getSqlSession().insert(super.getStatement("insert"), chart);
    }

    @Override
    public int findByDtAndCoinPairToCount(final Long dt, final CoinPairEnum coinPair) {
        final Chart chart = new Chart();
        chart.setDt(dt);
        chart.setCoinPair(coinPair);
        return super.getSqlSession().selectOne(super.getStatement("findByDtAndCoinPairToCount"), chart);
    }

    @Override
    public List<ChartVo> getChartData(final CoinPairEnum coinPair) {
        final Chart chart = new Chart();
        chart.setCoinPair(coinPair);
        return super.getSqlSession().selectList(super.getStatement("getChartData"), chart);
    }

}
