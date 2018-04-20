package com.exchange.web.mq.listener;

import com.exchange.web.mq.service.UmsService;
import lombok.extern.slf4j.Slf4j;
import com.exchange.core.domain.dto.EmailConfirm;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = "SIGNUP_EMAIL_CONFIRM_QUEUE")
public class SignupEmailConfirmQueueListener {

	@Autowired private UmsService umsService;

	@RabbitHandler
    public void onRecieve(final EmailConfirm emailConfirm) {
		try {
			umsService.sendSignupEmailConfirm(emailConfirm);
		} catch (Exception e) {
			log.error("注册邮箱验证 发送错误: {}", e.getMessage());
		}
    }

}
