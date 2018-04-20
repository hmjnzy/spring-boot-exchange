package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import java.util.List;

public interface TradeCoinDao {
    List<TradeCoin> findActiveToCoinPair(ActiveEnum activeEnum);
    TradeCoin findByCoinPairToActive(CoinPairEnum coinPairEnum);
}
