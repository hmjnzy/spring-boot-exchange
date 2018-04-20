package com.exchange.core.provider.feign;

import com.exchange.core.config.FeignConfig;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.PoloniexVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@FeignClient(name = "poloniexProvider", url = "https://poloniex.com/public",
        configuration = FeignConfig.class, decode404 = true, fallback = PoloniexProvider.DefaultFallback.class )
public interface PoloniexProvider {

    @RequestMapping(method = RequestMethod.GET, value = "?command=returnTradeHistory&currencyPair={currencyPair}&start={startTime}&end={endTime}")
    List<PoloniexVo.TradeHistory> tradeHistory(@PathVariable("currencyPair") String currencyPair,
                                               @PathVariable("startTime") Long startTime, @PathVariable("endTime") Long endTime);

    @RequestMapping(method = RequestMethod.GET, value = "?command=returnChartData&currencyPair={currencyPair}&start={startTime}&end={endTime}&period={period}")
    List<PoloniexVo.Ticker> ticker(@PathVariable("currencyPair") CoinPairEnum currencyPair, @PathVariable("startTime") Long startTime,
                                   @PathVariable("endTime") Long endTime, @PathVariable("period") Long period);

    @Slf4j
    @Component
    class DefaultFallback implements PoloniexProvider {

        @Override
        public List<PoloniexVo.TradeHistory> tradeHistory(@PathVariable("currencyPair") String currencyPair,
                                                          @PathVariable("startTime") Long startTime, @PathVariable("endTime") Long endTime) {
            log.error("-------------DefaultFallback tradeHistory 容错处理 {}, {}, {}", currencyPair, startTime, endTime);
            return null;
        }

        @Override
        public List<PoloniexVo.Ticker> ticker(@PathVariable("currencyPair") CoinPairEnum currencyPair, @PathVariable("startTime") Long startTime,
                                              @PathVariable("endTime") Long endTime, @PathVariable("period") Long period) {
            log.error("-------------DefaultFallback ticker 容错处理 {}, {}, {}, {}", currencyPair, startTime, endTime, period);
            return null;
        }
    }

}
