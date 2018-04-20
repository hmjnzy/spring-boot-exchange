package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.User;
import com.exchange.core.repository.dao.BaseDao;
import com.exchange.core.repository.dao.UserDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

    @Override
    public User findOneByEmailToAllFields(final String email) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByEmailToAllFields"), email);
    }

    @Override
    public User findOneByEmailToIdAndEmail(final String email) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByEmailToIdAndEmail"), email);
    }

    @Override
    public int findByEmailToCount(final String email) {
        return super.getSqlSession().selectOne(super.getStatement("findByEmailToCount"), email);
    }

    @Override
    public int insert(final User user) {
        return super.getSqlSession().insert(super.getStatement("insert"), user);
    }

    @Override
    public int updateActive(final User user) {
        return super.getSqlSession().update(super.getStatement("updateActive"), user);
    }

    @Override
    public int updateOtpHash(final String email, final String otpHash) {
        final User user = new User();
        user.setOtpHash(otpHash);
        user.setEmail(email);
        return super.getSqlSession().update(super.getStatement("updateOtpHash"), user);
    }

    @Override
    public List<User> findAllToId() {
        return super.getSqlSession().selectList(super.getStatement("findAllToId"));
    }

    @Override
    public User findOneByIdToAllFields(final Long id) {
        return super.getSqlSession().selectOne(super.getStatement("findOneByIdToAllFields"), id);
    }

}
