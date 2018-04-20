package com.exchange.core.service;

import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.repository.dao.TradeCoinDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeCoinService {

    private final TradeCoinDao tradeCoinDao;

    @Autowired
    public TradeCoinService(TradeCoinDao tradeCoinDao) {
        this.tradeCoinDao = tradeCoinDao;
    }

    public List<TradeCoin> findActiveToCoinPair(ActiveEnum activeEnum) {
        return tradeCoinDao.findActiveToCoinPair(activeEnum);
    }

    public TradeCoin findByCoinPairToActive(final CoinPairEnum coinPairEnum) {
        return tradeCoinDao.findByCoinPairToActive(coinPairEnum);
    }

    public CoinPairEnum isCoinPair(String coinPair) {
        CoinPairEnum pCoinPairEnum = null;
        for (CoinPairEnum coinPairEnum : CoinPairEnum.values()) {
            if (coinPairEnum.name().equalsIgnoreCase(coinPair)) {
                pCoinPairEnum = coinPairEnum;
                break;
            }
        }
        return pCoinPairEnum;
    }

    public List<String> splitCoinPair(CoinPairEnum coinPairEnum) {
        return Arrays.asList( coinPairEnum.name().split("[_]+") )
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

}
