package com.exchange.core.provider;

import com.exchange.core.config.RabbitMqConfig;
import com.exchange.core.domain.dto.EmailConfirm;
import com.exchange.core.domain.vo.WalletVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MqPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MqPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void signupEmailConfirm(final EmailConfirm emailConfirm) {

        log.info("发送邮件: {}", emailConfirm);

        this.rabbitTemplate.convertAndSend(RabbitMqConfig.SIGNUP_EMAIL_CONFIRM_QUEUE, emailConfirm);
    }

    public void depositTransaction(final WalletVo.TransactionInfo transactionInfo) {

        log.info("充值: {}", transactionInfo);

        rabbitTemplate.convertAndSend(RabbitMqConfig.DEPOSIT_TRANSACTIONS_QUEUE, transactionInfo);
    }

    public void withdrawalTransaction(final WalletVo.TransactionInfo transactionInfo) {

        log.info("提现: {}", transactionInfo);

        this.rabbitTemplate.convertAndSend(RabbitMqConfig.WITHDRAWAL_TRANSACTIONS_QUEUE, transactionInfo);
    }

//    public void websockMessagePublish(final MessagePacket messagePacket) {
//        rabbitTemplate.convertAndSend("exchange", "websock_message", messagePacket);
//    }
}
