package com.exchange.web.mq.service;

import com.exchange.core.domain.dto.EmailConfirm;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UmsService {

    @Value("${spring.mail.username}")
    public String ADMIN_EMAIL;

    @Value("${spring.mail.sendername}")
    public String ADMIN_NICKNAME;

    @Value("${base.url}")
    public String BASE_URL;

    private final EmailService emailService;

    @Autowired
    public UmsService(EmailService emailService) {
        this.emailService = emailService;
    }

    public MimeMessage sendSignupEmailConfirm(final EmailConfirm emailConfirm) throws UnsupportedEncodingException, CannotSendEmailException {
        final Map<String, Object> model = new HashMap<>();
        model.put("base_url", BASE_URL);
        model.put("confirm_url", BASE_URL + "/emailConfirm" + "?hash=" + emailConfirm.getHashEmail() + "&code=" + emailConfirm.getCode());
        return emailService.send(this.setEmail(emailConfirm.getEmail()), "signup", model);
    }

    public Email setEmail(final String toEmail) throws UnsupportedEncodingException {
        return DefaultEmail.builder()
                .from(new InternetAddress(ADMIN_EMAIL, ADMIN_NICKNAME))
                .to(Lists.newArrayList(new InternetAddress(toEmail, "to " + toEmail)))
                .subject("注册邮件")
                .body("- body -")
                .encoding("UTF-8")
                .build();
    }
}
