package com.exchange.web.master.controller.batch;

import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.service.batch.WithdrawalTransactionBatchService;
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
@RequestMapping("/batch/withdrawalTransaction")
public class WithdrawalTransactionBatchController {

    final WithdrawalTransactionBatchService withdrawalTransactionBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public WithdrawalTransactionBatchController(WithdrawalTransactionBatchService withdrawalTransactionBatchService, Environment environment, RedissonClient redissonClient) {
        this.withdrawalTransactionBatchService = withdrawalTransactionBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }
    //600_000
    @Scheduled(fixedDelay=5_000, initialDelay=5_000)
    private synchronized void doWithdrawalTransaction() {
        final String key = environment.getActiveProfiles()[0] + "_withdrawal_transaction_lock";

        //log.info("-------------------DoWithdrawalTransaction ready: {}", key);

        final RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(5_000L, TimeUnit.MILLISECONDS);

            //log.info("-------------------DoWithdrawalTransaction begin");

            for (CoinEnum coinEnum : CoinEnum.values()) {
                try {
                    withdrawalTransactionBatchService.doPublishTransaction(coinEnum);
                } catch (Exception e) {
                    log.error("Do Withdrawal Transaction: {}, {}", coinEnum.name(), e.getMessage());
                }
            }

            //log.info("-------------------DoWithdrawalTransaction end");
            lock.unlock();
        }
    }

    @GetMapping("/test")
    public String test() throws Exception {
        withdrawalTransactionBatchService.doPublishTransaction(CoinEnum.ETH);
        return "ok";
    }

}
