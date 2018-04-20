package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.ManualTransaction;
import com.exchange.core.domain.enums.StatusEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.ManualTransactionDao;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public class ManualTransactionDaoImpl extends BaseDao implements ManualTransactionDao {

    @Override
    public ManualTransaction findOneByIdAndUserIdToAllFields(final ManualTransaction manualTransaction) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdAndUserIdToAllFields"), manualTransaction);
    }

    @Override
    public int insert(final ManualTransaction manualTransaction) {
        return super.getSqlSession().insert(super.getStatement("insert"), manualTransaction);
    }

    @Override
    public int findByIdAndUserIdToCount(final ManualTransaction manualTransaction) {
        return super.getSqlSession().selectOne(super.getStatement("findByIdAndUserIdToCount"), manualTransaction);
    }

    @Override
    public ManualTransaction findByIdAndUserIdToRegDt(final String id) {
        return super.getSqlSession().selectOne(super.getStatement("findByIdAndUserIdToRegDt"), id);
    }

    @Override
    public int updateCompleteStatus(final String id, final Long userId, final StatusEnum status, final LocalDateTime regDt) {
        final ManualTransaction manualTransaction = new ManualTransaction();
        manualTransaction.setId(id);
        manualTransaction.setUserId(userId);
        manualTransaction.setStatus(status);
        manualTransaction.setRegDt(regDt);
        return super.getSqlSession().update(super.getStatement("updateCompleteStatus"), manualTransaction);
    }

}
