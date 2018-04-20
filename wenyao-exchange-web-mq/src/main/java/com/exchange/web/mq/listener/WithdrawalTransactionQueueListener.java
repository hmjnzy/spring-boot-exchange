package com.exchange.web.mq.listener;

import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.service.batch.WithdrawalTransactionBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = "WITHDRAWAL_TRANSACTIONS_QUEUE")
public class WithdrawalTransactionQueueListener {

    @Autowired private WithdrawalTransactionBatchService withdrawalTransactionBatchService;

    @RabbitHandler
    public void onRecieve(final WalletVo.TransactionInfo transactionInfo) {
        log.info("提现 onRecieve: {}", transactionInfo);
        try {
            withdrawalTransactionBatchService.doTransaction(transactionInfo);
        } catch(Exception e) {
            log.error("提现 onRecieve: {}", e.getMessage());
        }
    }
}
