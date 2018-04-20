package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinEnum;
import java.util.List;

public interface CoinDao {
    Coin findOneByNameToAllFields(String name);
    List<Coin> findAllToName();
    List<Coin> findActiveToName(ActiveEnum activeEnum);
    Coin findOneByNameToWithdrawalAmountAndWithdrawalFeeAmount(CoinEnum coinEnum);
    Coin findOneByNameAndActiveToPageInfo(CoinEnum coinEnum, ActiveEnum activeEnum);
    Coin findOneByNameToTradingFeePercentAndTradingMinAmount(CoinEnum coinEnum, ActiveEnum activeEnum);
    Coin findOneByNameToAutoCollectMinAmountAndDepositAllowConfirmation(CoinEnum coinEnum);
    int findOneByNameAndActiveToCount(CoinEnum coinEnum, ActiveEnum activeEnum);
}
