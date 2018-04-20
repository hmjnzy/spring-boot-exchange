package com.exchange.core.provider.feign;

import com.exchange.core.config.FeignConfig;
import com.exchange.core.domain.vo.CoinMarketCapVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@FeignClient(name = "coinMarketCapProvider", url = "https://api.coinmarketcap.com", 
	configuration = FeignConfig.class, decode404 = true, fallback = CoinMarketCapProvider.DefaultFallback.class )
public interface CoinMarketCapProvider {

    @RequestMapping(method = RequestMethod.GET, value = "/v1/ticker/?convert=USDT&limit=1")
    List<CoinMarketCapVo.Ticker> tickers();

    @RequestMapping(method = RequestMethod.GET, value = "/v1/ticker/{coinName}/?convert=USDT")
    List<CoinMarketCapVo.Ticker> ticker(@PathVariable("coinName") String coinName);
    
    @Slf4j
    @Component
    class DefaultFallback implements CoinMarketCapProvider {
    	
        @Override
        public List<CoinMarketCapVo.Ticker> tickers() {
        	
            log.error("-------------DefaultFallback 容错处理");
            return null;
        }
        
        @Override
        public List<CoinMarketCapVo.Ticker> ticker(@PathVariable("coinName") String coinName) {
        	
            log.error("-------------DefaultFallback 容错处理 {}", coinName);
            return null;
        }
    }
    
}
