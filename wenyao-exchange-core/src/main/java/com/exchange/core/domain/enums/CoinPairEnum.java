package com.exchange.core.domain.enums;

public enum CoinPairEnum {

    USDT_BTC, USDT_LTC, USDT_DASH, USDT_ETH;

    public static boolean contains(final String coinPair) {
        for (CoinPairEnum typeEnum : CoinPairEnum.values()) {
            if (typeEnum.name().equals(coinPair)) {
                return true;
            }
        }
        return false;
    }
}
