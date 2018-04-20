package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.WalletDao;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class WalletDaoImpl extends BaseDao implements WalletDao {

    @Override
    public int findByUserIdAndCoinToCount(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findByUserIdAndCoinToCount"), wallet);
    }

    @Override
    public int insert(final Wallet wallet) {
        return super.getSqlSession().insert(super.getStatement("insert"), wallet);
    }

    @Override
    public Wallet findOneByUserIdAndCoin(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByUserIdAndCoin"), wallet);
    }

    @Override
    public int updateByUserIdAndCoinToAll(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByUserIdAndCoinToAll"), wallet);
    }

    @Override
    public Wallet findOneByUserIdAndCoinToBalance(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByUserIdAndCoinToBalance"), wallet);
    }

    @Override
    public Wallet findOneByCoinAndAddressToUserId(final CoinEnum coinEnum, final String address) {
        final Wallet wallet = new Wallet();
        wallet.setCoinName(coinEnum);
        wallet.setAddress(address);
        return super.getSqlSession().selectOne(super.getStatement("findOneByCoinAndAddressToUserId"), wallet);
    }

    @Override
    public Wallet findOneByUserIdAndCoinToUsingBalance(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByUserIdAndCoinToUsingBalance"), wallet);
    }

    @Override
    public Wallet findOneByUserIdAndCoinToAvailableBalance(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByUserIdAndCoinToAvailableBalance"), wallet);
    }

    @Override
    public Wallet findOneByUserIdAndCoinToUsingBalanceAndAvailableBalance(final Long userId, final CoinEnum coinEnum) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByUserIdAndCoinToUsingBalanceAndAvailableBalance"), wallet);
    }

    @Override
    public int updateByIdToAllBalance(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByIdToAllBalance"), wallet);
    }

    @Override
    public int updateByIdToUsingBalance(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByIdToUsingBalance"), wallet);
    }

    @Override
    public int updateByIdToAvailableBalance(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByIdToAvailableBalance"), wallet);
    }

    @Override
    public int updateByUserIdAndCoinToAvailableBalance(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByUserIdAndCoinToAvailableBalance"), wallet);
    }

    @Override
    public int updateByIdToUsingBalanceAndAvailableBalance(Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByIdToUsingBalanceAndAvailableBalance"), wallet);
    }

    @Override
    public List<Wallet> findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId(final Long userId, final BigDecimal todayWithdrawalTotalBalance) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setTodayWithdrawalTotalBalance(todayWithdrawalTotalBalance);
        return super.getSqlSession().selectList(super.getStatement("findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId"), wallet);
    }

    @Override
    public List<Wallet> findAllByCoinToAddress(final CoinEnum coinEnum) {
        return super.getSqlSession().selectList(super.getStatement("findAllByCoinToAddress"), coinEnum);
    }

    @Override
    public int updateByIdToTodayWithdrawalTotalBalance(final Wallet wallet) {
        return super.getSqlSession().update(super.getStatement("updateByIdToTodayWithdrawalTotalBalance"), wallet);
    }

}
