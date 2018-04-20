package com.exchange.web.exchange.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import com.exchange.web.exchange.auth.CurrentUserDetailsService;
import com.exchange.web.exchange.filter.CsrfHeaderFilter;
import com.exchange.web.exchange.handler.CustomAuthenticationFailureHandler;
import com.exchange.web.exchange.handler.CustomAuthenticationSuccessHandler;

@EnableWebSecurity 
@Configuration
@EnableRedisHttpSession(redisNamespace = "user_session")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public UserDetailsService userDetailsService() {
    	return new CurrentUserDetailsService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers(
                        "/bootstrap/**/**", "/css/**", "/images/**","/js/**","/sign",
                        "/signup", "/doSignup", "/emailConfirm", "/signupEmail",
                            "/trade", "/api/wallet/getMyWalletsTradeCoin",
                        "/api/trade/sell",
                        "/api/trade/buy",
                        "/api/trade/cancel",
                        "/api/trade/getRealTimeOrders",
                        "/api/trade/getMarketHistoryOrders",
                        "/api/trade/getMyOrders",
                        "/api/trade/getMyHistoryOrders"
                        ).permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                    .formLogin()
                    .loginPage("/sign")
                    .failureUrl("/sign")
                    .failureHandler(authenticationFailureHandler())
                    .successHandler(authenticationSuccessHandler())
                    .loginProcessingUrl("/j_spring_security_check")
                    .defaultSuccessUrl("/dashboard")
                    .usernameParameter("email")
                    .passwordParameter("pwd").permitAll()
                .and()
                    .logout()
                    .logoutUrl("/signout")
                    .deleteCookies("wenyao_exchange")
                    .logoutSuccessUrl("/").permitAll()
                .and()
                    .rememberMe()
                .and()
                    .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                    .csrf().csrfTokenRepository(csrfTokenRepository()).disable();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

}
