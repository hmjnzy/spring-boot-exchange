package com.exchange.core.domain.vo;

import lombok.Data;

public class CoinMarketCapVo {
	/*
        "id": "bitcoin", 
        "name": "Bitcoin", 
        "symbol": "BTC", 
        "rank": "1", 
        "price_usd": "8742.44", 
        "price_btc": "1.0", 
        "24h_volume_usd": "5853570000.0", 
        "market_cap_usd": "148039014935", 
        "available_supply": "16933375.0", 
        "total_supply": "16933375.0", 
        "max_supply": "21000000.0", 
        "percent_change_1h": "0.14", 
        "percent_change_24h": "-3.89", 
        "percent_change_7d": "6.72", 
        "last_updated": "1521724765", 
        "price_usdt": "8733.21819555", 
        "24h_volume_usdt": "5847395467.73", 
        "market_cap_usdt": "147882858662"
	 * */
    @Data
    public static class Ticker {
        private String id;
        private String name;
        private String symbol;
        private String rank;
        private String price_usd;
        private String price_btc;
        //private String 24h_volume_usd;
        private String market_cap_usd;
        private String available_supply;
        private String total_supply;
        private String max_supply;
        private String percent_change_1h;
        private String percent_change_24h;
        private String percent_change_7d;
        private String last_updated;
        private String price_usdt;
        //private String 24h_volume_usdt;
        private String market_cap_usdt;
    }
    
}
