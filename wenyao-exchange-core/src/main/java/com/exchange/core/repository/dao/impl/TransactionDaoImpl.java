package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Transaction;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import com.exchange.core.domain.vo.TransactionVo;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.TransactionDao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TransactionDaoImpl extends BaseDao implements TransactionDao {

    @Override
    public List<Transaction> findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc(final Long userId, final TransactionVo.ReqTransactions reqTransactions) {
        PageHelper.startPage(reqTransactions.getPageNo(), reqTransactions.getPageSize());
        final Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setCoinName(reqTransactions.getCoinName());
        transaction.setCategory(reqTransactions.getCategory());
        return super.getSqlSession().selectList(super.getStatement("findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc"), transaction);
    }

    @Override
    public List<Transaction> findAllByCoinAndCategoryAndStatusOrderByRegDtASC(final TransactionVo.ReqTransactions reqTransactions) {
        PageHelper.startPage(reqTransactions.getPageNo(), reqTransactions.getPageSize());
        final Transaction transaction = new Transaction();
        transaction.setCoinName(reqTransactions.getCoinName());
        transaction.setCategory(reqTransactions.getCategory());
        transaction.setStatus(reqTransactions.getStatus());
        return super.getSqlSession().selectList(super.getStatement("findAllByCoinAndCategoryAndStatusOrderByRegDtASC"), transaction);
    }

    @Override
    public int findByIdAndUserIdToCount(final Transaction transaction) {
        return super.getSqlSession().selectOne(super.getStatement("findByIdAndUserIdToCount"), transaction);
    }

    @Override
    public Transaction findOneByCoinAndTxId(final CoinEnum coinName, final String txId) {
        final Transaction transaction = new Transaction();
        transaction.setCoinName(coinName);
        transaction.setTxId(txId);
        return super.getSqlSession().selectOne(super.getStatement("findOneByCoinAndTxId"), transaction);
    }

    @Override
    public Transaction findOneByIdToRegDtAndStatus(final String id, final Long userId, final CoinEnum coinName) {
        final Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUserId(userId);
        transaction.setCoinName(coinName);
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdToRegDtAndStatus"), transaction);
    }

    @Override
    public int insert(final Transaction transaction) {
        return super.getSqlSession().insert(super.getStatement("insert"), transaction);
    }

    @Override
    public int updateCompleteStatus(final String id, final String txId, final StatusEnum status, final BigInteger confirmation, final LocalDateTime completeDtm, final LocalDateTime regDt) {
        final Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTxId(txId);
        transaction.setStatus(status);
        transaction.setConfirmation(confirmation);
        transaction.setCompleteDtm(completeDtm);
        transaction.setRegDt(regDt);
        return super.getSqlSession().update(super.getStatement("updateCompleteStatus"), transaction);
    }

}
