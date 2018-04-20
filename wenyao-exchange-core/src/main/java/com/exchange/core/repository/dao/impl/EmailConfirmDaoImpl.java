package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.EmailConfirm;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.EmailConfirmDao;
import org.springframework.stereotype.Repository;

@Repository
public class EmailConfirmDaoImpl extends BaseDao implements EmailConfirmDao {

    @Override
    public EmailConfirm findOneByHashemailAndCode(final String hash, final String code) {
        EmailConfirm emailConfirm = new EmailConfirm();
        emailConfirm.setHashEmail(hash);
        emailConfirm.setCode(code);
        return super.getSqlSession().selectOne(super.getStatement("findOneByHashemailAndCode"), emailConfirm);
    }

    @Override
    public EmailConfirm findOneByEmail(final String email) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByEmail"), email);
    }

    @Override
    public void insert(final EmailConfirm emailConfirm) {
    	super.getSqlSession().insert(super.getStatement("insert"), emailConfirm);
    }

    @Override
    public int updateSendYn(EmailConfirm emailConfirm) {
        return super.getSqlSession().update(super.getStatement("updateSendYn"), emailConfirm);
    }
}
