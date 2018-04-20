package com.exchange.web.mq.listener;

import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.service.batch.DepositTransactionBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = "DEPOSIT_TRANSACTIONS_QUEUE")
public class DepositTransactionQueueListener {

    @Autowired private DepositTransactionBatchService depositTransactionBatchService;

    @RabbitHandler
    public void onRecieve(final WalletVo.TransactionInfo transactionInfo) {
        log.info("充值 onRecieve: {}", transactionInfo);
        try {
            depositTransactionBatchService.doTransaction(transactionInfo);
        } catch (Exception e) {
            log.error("充值 onRecieve: {}", e.getMessage());
        }
    }
}
