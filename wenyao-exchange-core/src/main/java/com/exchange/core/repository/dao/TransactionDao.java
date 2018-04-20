package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Transaction;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import com.exchange.core.domain.vo.TransactionVo;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDao {
    List<Transaction> findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc(Long userId, TransactionVo.ReqTransactions reqTransactions);
    List<Transaction> findAllByCoinAndCategoryAndStatusOrderByRegDtASC(TransactionVo.ReqTransactions reqTransactions);
    int findByIdAndUserIdToCount(Transaction transaction);
    Transaction findOneByCoinAndTxId(CoinEnum coinName, String txId);
    Transaction findOneByIdToRegDtAndStatus(String id, Long userId, CoinEnum coinName);
    int insert(Transaction transaction);
    int updateCompleteStatus(String id, String txId, StatusEnum status, BigInteger confirmation, LocalDateTime completeDtm, LocalDateTime regDt);
}
