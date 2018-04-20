package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.Level;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.LevelEnum;

public interface LevelDao {
    Level findOneByCoinNameAndLevel(CoinEnum coinEnum, LevelEnum levelEnum);
}
