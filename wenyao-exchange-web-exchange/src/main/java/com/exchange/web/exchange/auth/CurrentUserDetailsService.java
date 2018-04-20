package com.exchange.web.exchange.auth;

import com.exchange.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.exchange.core.domain.dto.User;
import java.util.Optional;

@Slf4j
@Service
public class CurrentUserDetailsService implements UserDetailsService {

	@Autowired
    private UserService userService;
	
	public CurrentUserDetailsService() {
	}

	@Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        log.info("Authenticating user with loginId {}", email);

        User user = Optional.ofNullable(
                userService.findOneByEmailToAllFields(email)
        ).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with email=%s was not found", email))
        );
        return new CurrentUserVo(user);
    }
}
