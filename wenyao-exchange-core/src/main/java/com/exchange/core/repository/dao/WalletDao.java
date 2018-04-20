package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.CoinEnum;
import java.math.BigDecimal;
import java.util.List;

public interface WalletDao {
    int findByUserIdAndCoinToCount(Long userId, CoinEnum coinEnum);
    int insert(Wallet wallet);
    int updateByUserIdAndCoinToAll(Wallet wallet);
    Wallet findOneByUserIdAndCoin(Long userId, CoinEnum coinEnum);
    Wallet findOneByUserIdAndCoinToBalance(Long userId, CoinEnum coinEnum);
    Wallet findOneByCoinAndAddressToUserId(CoinEnum coinEnum, String address);
    Wallet findOneByUserIdAndCoinToUsingBalance(Long userId, CoinEnum coinEnum);
    Wallet findOneByUserIdAndCoinToAvailableBalance(Long userId, CoinEnum coinEnum);
    Wallet findOneByUserIdAndCoinToUsingBalanceAndAvailableBalance(Long userId, CoinEnum coinEnum);
    int updateByIdToAllBalance(Wallet wallet);
    int updateByIdToUsingBalance(Wallet wallet);
    int updateByIdToAvailableBalance(Wallet wallet);
    int updateByUserIdAndCoinToAvailableBalance(Wallet wallet);
    int updateByIdToUsingBalanceAndAvailableBalance(Wallet wallet);
    List<Wallet> findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId(Long userId, BigDecimal todayWithdrawalTotalBalance);
    List<Wallet> findAllByCoinToAddress(CoinEnum coinEnum);
    int updateByIdToTodayWithdrawalTotalBalance(Wallet wallet);
}
