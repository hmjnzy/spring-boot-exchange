package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.CoinDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CoinDaoImpl extends BaseDao implements CoinDao {

    @Override
    public Coin findOneByNameToAllFields(final String name) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameToAllFields"), name);
    }

    @Override
    public List<Coin> findAllToName() {
        return super.getSqlSession().selectList(super.getStatement("findAllToName"));
    }

    @Override
    public List<Coin> findActiveToName(final ActiveEnum activeEnum) {
        return super.getSqlSession().selectList(super.getStatement("findActiveToName"), activeEnum);
    }

    @Override
    public Coin findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount(final CoinEnum coinEnum) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount"), coinEnum);
    }

    @Override
    public Coin findOneByNameAndActiveToPageInfo(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        final Coin coin = new Coin();
        coin.setName(coinEnum);
        coin.setActive(activeEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameToPageInfo"), coin);
    }

    @Override
    public Coin findOneByNameToTradingFeePercentAndTradingMinAmount(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        final Coin coin = new Coin();
        coin.setName(coinEnum);
        coin.setActive(activeEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameToTradingFeePercentAndTradingMinAmount"), coin);
    }

    @Override
    public Coin findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation(CoinEnum coinEnum) {
        final Coin coin = new Coin();
        coin.setName(coinEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation"), coin);
    }

    @Override
    public int findOneByNameAndActiveToCount(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        final Coin coin = new Coin();
        coin.setName(coinEnum);
        coin.setActive(activeEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByNameAndActiveToCount"), coin);
    }

}
