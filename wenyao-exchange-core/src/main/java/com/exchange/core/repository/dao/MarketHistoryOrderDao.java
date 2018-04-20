package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.MarketHistoryOrder;
import com.exchange.core.domain.enums.CoinPairEnum;
import java.util.List;

public interface MarketHistoryOrderDao {
    int insert(MarketHistoryOrder marketHistoryOrder);
    List<MarketHistoryOrder> findAllByCoinPairOrderById(CoinPairEnum coinPair, Integer pageNo, Integer pageSize);
}
