package com.exchange.web.exchange.controller.rest;

import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chart")
public class ChartRestController {

    private final ChartService chartService;

    @Autowired
    public ChartRestController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/{coinPair}")
    public List<List<String>> chart(@PathVariable("coinPair") CoinPairEnum coinPair) {
        List<List<String>> data = chartService.getChartData(coinPair);
        return data;
    }

}
