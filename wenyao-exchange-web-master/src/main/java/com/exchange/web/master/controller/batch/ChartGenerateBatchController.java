package com.exchange.web.master.controller.batch;

import com.exchange.core.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/batch/chart")
public class ChartGenerateBatchController {

    private final ChartService chartService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public ChartGenerateBatchController(ChartService chartService, Environment environment, RedissonClient redissonClient) {
        this.chartService = chartService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

}
