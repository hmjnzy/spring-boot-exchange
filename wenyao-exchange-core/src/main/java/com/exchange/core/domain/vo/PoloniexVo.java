package com.exchange.core.domain.vo;

import lombok.Data;

public class PoloniexVo {
/*
[{
	"date": 1405699200,
	"high": 0.00413615,
	"low": 0.00403986,
	"open": 0.00404545,
	"close": 0.00403997,
	"volume": 4.95713239,
	"quoteVolume": 1205.10503896,
	"weightedAverage": 0.00411344
}]
* */
    @Data
    public static class Ticker {
        private String date;
        private String high;
        private String low;
        private String open;
        private String close;
        private String volume;
        private String quoteVolume;
        private String weightedAverage;
    }
/* command=returnTradeHistory&currencyPair=USDT_BTC&start=1522137480&end=1522137490
[{
	"globalTradeID": 357241433,
	"tradeID": 21284742,
	"date": "2018-03-27 07:58:08",
	"type": "sell",
	"rate": "7937.05882473",
	"amount": "0.00018894",
	"total": "1.49962789"
}]
* */
    @Data
    public static class TradeHistory {
        private String date;
        private String type;
        private String rate;
        private String amount;
        private String total;
    }

}
