package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.MyHistoryOrderDao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MyHistoryOrderDaoImpl extends BaseDao implements MyHistoryOrderDao {

    @Override
    public int insert(final MyHistoryOrder myHistoryOrder) {
        return super.getSqlSession().insert(super.getStatement("insert"), myHistoryOrder);
    }

    @Override
    public List<MyHistoryOrder> findAllByUserIdAndCoinPairOrderById(final Long userId, final CoinPairEnum coinPair, final Integer pageNo, final Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        final MyHistoryOrder myHistoryOrder = new MyHistoryOrder();
        myHistoryOrder.setUserId(userId);
        myHistoryOrder.setCoinPair(coinPair);
        return super.getSqlSession().selectList(super.getStatement("findAllByUserIdAndCoinPairOrderById"), myHistoryOrder);
    }

}
