package com.exchange.web.master.controller.batch;

import com.exchange.core.service.batch.ResetWithdrawalLimitTotalBalanceBatchService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/batch/resetWithdrawalLimitTotalBalance")
public class ResetWithdrawalLimitTotalBalanceBatchController {

    final ResetWithdrawalLimitTotalBalanceBatchService resetWithdrawalLimitTotalBalanceBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public ResetWithdrawalLimitTotalBalanceBatchController(ResetWithdrawalLimitTotalBalanceBatchService resetWithdrawalLimitTotalBalanceBatchService,
                                                           Environment environment, RedissonClient redissonClient) {
        this.resetWithdrawalLimitTotalBalanceBatchService = resetWithdrawalLimitTotalBalanceBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    /*
    * 24点整执行一次
    * */
    @Scheduled(cron = "0 0 0 * * ?")
    private synchronized void doResetWithdrawalLimitTotalBalance() {
        final String key = environment.getActiveProfiles()[0] + "_today_withdrawal_total_balance_reset_lock";

        log.info("-------------------DoResetWithdrawalLimitTotalBalance ready: {}", key);

        final RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(86400L, TimeUnit.SECONDS);

            log.info("-------------------DoResetWithdrawalLimitTotalBalance begin");

            resetWithdrawalLimitTotalBalanceBatchService.dailyReset();

            log.info("-------------------DoResetWithdrawalLimitTotalBalance end");

            lock.unlock();
        }
    }

}
