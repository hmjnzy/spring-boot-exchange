package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.Level;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.LevelEnum;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.LevelDao;
import org.springframework.stereotype.Repository;

@Repository
public class LevelDaoImpl extends BaseDao implements LevelDao {

    @Override
    public Level findOneByCoinNameAndLevel(CoinEnum coinEnum, LevelEnum levelEnum) {
        Level level = new Level();
        level.setCoinName(coinEnum);
        level.setLevel(levelEnum);
        return super.getSqlSession().selectOne(super.getStatement("findOneByCoinNameAndLevel"), level);
    }
    /*
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
    * */
}
