package com.exchange.web.master.controller.batch;

import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.service.batch.DepositTransactionBatchService;
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
@RequestMapping("/batch/depositTransaction")
public class DepositTransactionBatchController {

    final DepositTransactionBatchService depositTransactionBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public DepositTransactionBatchController(DepositTransactionBatchService depositTransactionBatchService, Environment environment, RedissonClient redissonClient) {
        this.depositTransactionBatchService = depositTransactionBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(fixedDelay=10_000, initialDelay=2_000)
    private synchronized void doPublishDepositTransaction() {

        final String key = environment.getActiveProfiles()[0] + "_deposit_transaction_lock";

        //log.info("-------------------DoDepositTransaction ready: {}", key);

        final RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(10_000L, TimeUnit.MILLISECONDS);

            log.info("-------------------DoDepositTransaction begin");

            for (CoinEnum coinEnum : CoinEnum.values()) {
                try {
                    depositTransactionBatchService.doPublishTransaction(coinEnum);
                } catch (Exception e) {
                    log.error("Do Deposit Transaction: {}, {}", coinEnum.name(), e.getMessage());
                }
            }

            log.info("-------------------DoDepositTransaction end");
            lock.unlock();
        }
    }

    @GetMapping("/test")
    public String test() throws Exception {
        depositTransactionBatchService.doPublishTransaction(CoinEnum.ETH);
        return "ok";
    }

}
