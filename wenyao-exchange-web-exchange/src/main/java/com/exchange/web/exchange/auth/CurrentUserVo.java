package com.exchange.web.exchange.auth;

import com.exchange.core.domain.vo.UserVo;
import org.springframework.security.core.authority.AuthorityUtils;
import com.exchange.core.domain.dto.User;

public class CurrentUserVo extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -375006020027286036L;

	private UserVo.CurrentUser currentUser;

    public CurrentUserVo(User user) {
        super(user.getEmail(), user.getPwd(), AuthorityUtils.createAuthorityList("ROLE_" + user.getRole()));
        currentUser = new UserVo.CurrentUser();
        currentUser.setId(user.getId());
        currentUser.setEmail(user.getEmail());
    }

    public UserVo.CurrentUser getCurrentUser() {
        return currentUser;
    }

    public Long getId() {return currentUser.getId();}

    @Override
    public String toString() {
        return "CurrentUserVo{UserVo.CurrentUser " + currentUser + "} " + super.toString();
    }
}
