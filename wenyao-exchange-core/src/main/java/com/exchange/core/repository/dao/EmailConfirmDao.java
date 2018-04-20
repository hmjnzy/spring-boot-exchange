package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.EmailConfirm;
import com.exchange.core.domain.vo.EmailConfirmVo;

public interface EmailConfirmDao {
    EmailConfirm findOneByHashemailAndCode(String hash, String code);
    EmailConfirm findOneByEmail(String email);
    void insert(EmailConfirm emailConfirm);
    int updateSendYn(EmailConfirm emailConfirm);
}
