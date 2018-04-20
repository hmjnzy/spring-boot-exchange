package com.exchange.core.service;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.repository.dao.CoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoinService {

    private final CoinDao coinDao;

    @Autowired
    public CoinService(CoinDao coinDao) {
        this.coinDao = coinDao;
    }

    public Coin findOneByNameToAllFields(final String name) {
        return coinDao.findOneByNameToAllFields(name);
    }

    public Coin findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount(final CoinEnum coinEnum) {
        return coinDao.findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount(coinEnum);
    }

    public Coin findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation(CoinEnum coinEnum) {
        return coinDao.findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation(coinEnum);
    }

    public List<Coin> findAllToName() {
        return coinDao.findAllToName();
    }

    public List<Coin> findActiveToName(final ActiveEnum activeEnum) {
        return coinDao.findActiveToName(activeEnum);
    }

    public Coin findOneByNameAndActiveToPageInfo(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        return coinDao.findOneByNameAndActiveToPageInfo(coinEnum, activeEnum);
    }

    public Coin findOneByNameToTradingFeePercentAndTradingMinAmount(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        return coinDao.findOneByNameToTradingFeePercentAndTradingMinAmount(coinEnum, activeEnum);
    }

    public int findOneByNameAndActiveToCount(final CoinEnum coinEnum, final ActiveEnum activeEnum) {
        return coinDao.findOneByNameAndActiveToCount(coinEnum, activeEnum);
    }

    public CoinEnum isCoin(final String coin) {
        CoinEnum pCoinEnum = null;
        for (CoinEnum coinEnum : CoinEnum.values()) {
            if (coinEnum.name().equalsIgnoreCase(coin)) {
                pCoinEnum = coinEnum;
                break;
            }
        }
        return pCoinEnum;
    }

}
