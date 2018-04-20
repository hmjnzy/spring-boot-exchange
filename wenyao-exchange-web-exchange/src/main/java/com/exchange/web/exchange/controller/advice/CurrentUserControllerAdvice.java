package com.exchange.web.exchange.controller.advice;

import com.exchange.core.domain.vo.UserVo;
import com.exchange.web.exchange.auth.CurrentUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice(basePackages = "com.exchange.web.exchange.controller")
@Order(1)
public class CurrentUserControllerAdvice {

    private final Environment environment;

    @Autowired
    public CurrentUserControllerAdvice(final Environment environment) {
        this.environment = environment;
    }

    @ModelAttribute("user")
    public UserVo.CurrentUser getCurrentUser(final Authentication authentication) {
        return (authentication == null) ? null : ((CurrentUserVo) authentication.getPrincipal()).getCurrentUser();
    }
    @ModelAttribute("dummy")
    public long getVersion() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        return timestamp.getTime();
    }

    @ModelAttribute("base_url")
    public String getBaseUrl() {
        return environment.getProperty("base.url");
    }
}
