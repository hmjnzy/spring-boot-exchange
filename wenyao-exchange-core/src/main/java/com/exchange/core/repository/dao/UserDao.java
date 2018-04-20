package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.User;
import java.util.List;

public interface UserDao {
    int insert(User user);
    int findByEmailToCount(String email);
    User findOneByEmailToAllFields(String email);
    User findOneByEmailToIdAndEmail(String email);
    int updateActive(User user);
    int updateOtpHash(String email, String otpHash);
    List<User> findAllToId();
    User findOneByIdToAllFields(Long id);
}
