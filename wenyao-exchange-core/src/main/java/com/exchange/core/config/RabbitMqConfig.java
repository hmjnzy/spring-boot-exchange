package com.exchange.core.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String SIGNUP_EMAIL_CONFIRM_QUEUE = "SIGNUP_EMAIL_CONFIRM_QUEUE";

    public static final String DEPOSIT_TRANSACTIONS_QUEUE = "DEPOSIT_TRANSACTIONS_QUEUE";

    public static final String WITHDRAWAL_TRANSACTIONS_QUEUE = "WITHDRAWAL_TRANSACTIONS_QUEUE";

    @Bean
    public Queue emailConfirmQueue() {
        return new Queue(SIGNUP_EMAIL_CONFIRM_QUEUE, true);
    }

    @Bean
    public Queue depositTransactionQueue() {
        return new Queue(DEPOSIT_TRANSACTIONS_QUEUE, true);
    }

    @Bean
    public Queue withdrawalTransactionQueue() {
        return new Queue(WITHDRAWAL_TRANSACTIONS_QUEUE, true);
    }

}
