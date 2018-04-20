package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.enums.CoinPairEnum;
import java.util.List;

public interface MyHistoryOrderDao {
    int insert(MyHistoryOrder myHistoryOrder);
    List<MyHistoryOrder> findAllByUserIdAndCoinPairOrderById(Long userId, CoinPairEnum coinPair, Integer pageNo, Integer pageSize);
}
