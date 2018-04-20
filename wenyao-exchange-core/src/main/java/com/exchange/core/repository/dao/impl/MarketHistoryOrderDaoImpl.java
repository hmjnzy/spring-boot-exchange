package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.MarketHistoryOrder;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.MarketHistoryOrderDao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MarketHistoryOrderDaoImpl extends BaseDao implements MarketHistoryOrderDao {

    @Override
    public int insert(final MarketHistoryOrder marketHistoryOrder) {
        return super.getSqlSession().insert(super.getStatement("insert"), marketHistoryOrder);
    }

    @Override
    public List<MarketHistoryOrder> findAllByCoinPairOrderById(final CoinPairEnum coinPair, final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final MarketHistoryOrder marketHistoryOrder = new MarketHistoryOrder();
        marketHistoryOrder.setCoinPair(coinPair);
        return super.getSqlSession().selectList(super.getStatement("findAllByCoinPairOrderById"), marketHistoryOrder);
    }

}
