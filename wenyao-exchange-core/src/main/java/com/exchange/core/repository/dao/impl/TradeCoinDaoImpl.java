package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.TradeCoinDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TradeCoinDaoImpl extends BaseDao implements TradeCoinDao {

    @Override
    public List<TradeCoin> findActiveToCoinPair(final ActiveEnum activeEnum) {
        return super.getSqlSession().selectList(super.getStatement("findActiveToCoinPair"), activeEnum);
    }

    @Override
    public TradeCoin findByCoinPairToActive(final CoinPairEnum coinPairEnum) {
        return super.getSqlSession().selectOne(super.getStatement("findByCoinPairToActive"), coinPairEnum);
    }

}
