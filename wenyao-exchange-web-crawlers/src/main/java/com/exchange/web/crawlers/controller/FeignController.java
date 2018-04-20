package com.exchange.web.crawlers.controller;

import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.exchange.core.domain.vo.CoinMarketCapVo;
import com.exchange.core.provider.feign.CoinMarketCapProvider;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/feign")
public class FeignController {
	
    private final CoinMarketCapProvider coinMarketCapProvider;

    @Autowired
    public FeignController(CoinMarketCapProvider coinMarketCapProvider) {
        this.coinMarketCapProvider = coinMarketCapProvider;
    }

    @RequestMapping(value = "coinMarketCap/{coinName}", method = RequestMethod.GET)
    public Response<CoinMarketCapVo.Ticker> coinMarketCap(@PathVariable("coinName") String coinName) {

        log.info("-------------coinMarketCap coinName: {}", coinName);

        List<CoinMarketCapVo.Ticker> tickers = coinMarketCapProvider.ticker(coinName);
        if (tickers.isEmpty()) {
            return Response.<CoinMarketCapVo.Ticker>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        } else {
            return Response.<CoinMarketCapVo.Ticker>builder()
                    .status(CodeEnum.SUCCESS.getStatus())
                    .message(CodeEnum.SUCCESS.getMessage())
                    .data(tickers.get(0))
                    .build();
        }
    }
   
}
