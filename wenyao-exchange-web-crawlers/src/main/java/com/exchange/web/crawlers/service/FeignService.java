package com.exchange.web.crawlers.service;

import com.exchange.core.domain.dto.Chart;
import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.MarketHistoryOrder;
import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.CrawlEnum;
import com.exchange.core.domain.enums.OrderType;
import com.exchange.core.domain.vo.PoloniexVo;
import com.exchange.core.service.ChartService;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.MarketHistoryOrderService;
import com.exchange.core.service.TradeCoinService;
import com.exchange.core.util.DateUtil;
import com.exchange.core.provider.feign.CoinMarketCapProvider;
import com.exchange.core.provider.feign.PoloniexProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FeignService {

    private final Environment environment;
    private final RedissonClient redissonClient;
    private final CoinMarketCapProvider coinMarketCapProvider;
    private final PoloniexProvider poloniexProvider;
    //private final CoinService coinService;
    private final ChartService chartService;
    private final TradeCoinService tradeCoinService;
    private final MarketHistoryOrderService marketHistoryOrderService;

    @Autowired
    public FeignService(CoinMarketCapProvider coinMarketCapProvider, PoloniexProvider poloniexProvider, MarketHistoryOrderService marketHistoryOrderService,
                        ChartService chartService, TradeCoinService tradeCoinService, Environment environment, RedissonClient redissonClient) {
        this.coinMarketCapProvider = coinMarketCapProvider;
        this.poloniexProvider = poloniexProvider;
        //this.coinService = coinService;CoinService coinService,
        this.marketHistoryOrderService = marketHistoryOrderService;
        this.chartService = chartService;
        this.tradeCoinService = tradeCoinService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(fixedDelay=6_000, initialDelay=0)
    private synchronized void getPoloniexChartData() {
        final String key = environment.getActiveProfiles()[0] + "_take_chart_data_lock";

        log.info("-------------------Poloniex ready: {}", key);

        final RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            final Long rateTime = 6L;
            lock.lock(rateTime, TimeUnit.SECONDS);

            log.info("-------------------Poloniex begin");

            final Long endMilliTime = System.currentTimeMillis();
            final Long endTime = endMilliTime / 1000L;
            final Long startTime = endTime - 1296000L;//[86400L, 3600L, 1800L, 900L, 6L]

            final List<TradeCoin> tradeCoins = tradeCoinService.findActiveToCoinPair(ActiveEnum.Y);

            for (TradeCoin tradeCoin : tradeCoins) {

                log.info("-------------------Poloniex currencyPair: {}", tradeCoin.getCoinPair());

                List<PoloniexVo.Ticker> tickers = poloniexProvider.ticker(tradeCoin.getCoinPair(), startTime, endTime, 300L);
                if (!tickers.isEmpty()) {

                    for (PoloniexVo.Ticker ticker : tickers) {

                        final Long dt = Long.parseLong(ticker.getDate());

                        if (chartService.findByDtAndCoinPairToCount(dt, tradeCoin.getCoinPair()) == 0) {
                            Chart chart = new Chart();
                            chart.setDt(dt);
                            chart.setCoinPair(tradeCoin.getCoinPair());
                            chart.setOpen(new BigDecimal(ticker.getOpen()));
                            chart.setHigh(new BigDecimal(ticker.getHigh()));
                            chart.setLow(new BigDecimal(ticker.getLow()));
                            chart.setClose(new BigDecimal(ticker.getClose()));
                            chart.setVolume(new BigDecimal(ticker.getVolume()));
                            chart.setCrawlFrom(CrawlEnum.POLONIEX);
                            chartService.insert(chart);
                        }
                    }
                    log.info("-------------------Poloniex ticker: {}", tickers.size());
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(255L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            final Long leftTime = 6_000L - (System.currentTimeMillis() - endMilliTime + 1L);

            log.info("-------------------Poloniex end to Sleep {} milli secends", leftTime);

            if (leftTime > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(leftTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lock.unlock();
        }
    }
/*
* TODO Take Poloniex Trade History
* */
//    @Scheduled(fixedDelay=1_000, initialDelay=0)
//    private synchronized void getPoloniexTradeHistory() {
//        final String key = environment.getActiveProfiles()[0] + "_take_trade_history_lock";
//
//        log.info("-------------------Poloniex ready: {}", key);
//
//        final RLock lock = redissonClient.getLock(key);
//        if (!lock.isLocked()) {
//            final Long rateTime = 3L;
//            lock.lock(rateTime, TimeUnit.SECONDS);
//
//            log.info("-------------------Poloniex begin");
//
//            final Long endMilliTime = System.currentTimeMillis();
//            final Long endTime = endMilliTime / 1000L;
//            final Long startTime = endTime - rateTime;
//            final List<Coin> coins = coinService.findActiveToName(ActiveEnum.Y);
//            for (Coin coin : coins) {
//
//                if (!CoinEnum.USDT.equals(coin.getName())) {
//
//                    String currencyPair = "USDT_" + coin.getUnit();
//
//                    log.info("-------------------Poloniex currencyPair: {}, {} , {}", currencyPair, startTime, endTime);
//
//                    List<PoloniexVo.TradeHistory> tradeHistorys = poloniexProvider.tradeHistory(currencyPair, startTime, endTime);
//                    if (!tradeHistorys.isEmpty()) {
//
//                        for (PoloniexVo.TradeHistory tradeHistory : tradeHistorys) {
//                            MarketHistoryOrder marketHistoryOrder = new MarketHistoryOrder();
//                            marketHistoryOrder.setUserId(0L);
//                            marketHistoryOrder.setOrderId(0L);
//                            marketHistoryOrder.setAmount(new BigDecimal(tradeHistory.getAmount()));
//                            marketHistoryOrder.setCompletedDtm(LocalDateTime.parse(tradeHistory.getDate(), DateUtil.FORMATTER_YYYY_MM_DD_HH_MM_SS));
//                            marketHistoryOrder.setDt(tradeHistory.getDate().substring(0, 16));
//
//                            if (tradeHistory.getType().equals(OrderType.SELL)) {
//                                marketHistoryOrder.setOrderType(OrderType.SELL);
//                                marketHistoryOrder.setFromCoinName(coin.getName());
//                                marketHistoryOrder.setToCoinName(CoinEnum.USDT);
//                            } else {
//                                marketHistoryOrder.setOrderType(OrderType.BUY);
//                                marketHistoryOrder.setFromCoinName(CoinEnum.USDT);
//                                marketHistoryOrder.setToCoinName(coin.getName());
//                            }
//
//                            marketHistoryOrder.setPrice(new BigDecimal(tradeHistory.getRate()));
//                            marketHistoryOrder.setCrawlFrom(CrawlEnum.POLONIEX);
//                            marketHistoryOrderService.insert(marketHistoryOrder);
//                        }
//
//                        log.info("-------------------Poloniex tradeHistorys: {}", tradeHistorys);
//                    }
//                    log.info("-------------------Poloniex currencyPair: {}", currencyPair);
//
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(255L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            final Long leftTime = 3000L - (System.currentTimeMillis() - endMilliTime + 1L);
//            if (leftTime > 0) {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(leftTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.info("-------------------Poloniex end {}, {}", leftTime, (System.currentTimeMillis() - endMilliTime + 1L));
//            lock.unlock();
//        }
//    }
/*
* TODO Take CoinMarketCap Ticker
* */
//    @Scheduled(fixedDelay=1_000, initialDelay=0)
//    private synchronized void getCoinMarketCap() {
//        final String key = environment.getActiveProfiles()[0] + "_take_ticker_lock";
//        log.info("-------------------CoinMarketCap ready: {}", key);
//        RLock lock = redissonClient.getLock(key);
//        if (!lock.isLocked()) {
//            lock.lock(1, TimeUnit.SECONDS);
//            log.info("-------------------CoinMarketCap begin");
//
//            log.info("-------------------CoinMarketCap coinName: {}, {}", "bitcoin", coinMarketCapProvider.ticker("bitcoin"));
//
//            log.info("-------------------CoinMarketCap end");
//            lock.unlock();
//        }
//    }
}
