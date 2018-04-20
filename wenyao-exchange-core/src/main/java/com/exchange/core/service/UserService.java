package com.exchange.core.service;

import com.exchange.core.annotation.SoftTransational;
import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.EmailConfirm;
import com.exchange.core.domain.dto.User;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.LevelEnum;
import com.exchange.core.domain.enums.RoleEnum;
import com.exchange.core.domain.vo.EmailConfirmVo;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.provider.MqPublisher;
import com.exchange.core.repository.dao.EmailConfirmDao;
import com.exchange.core.repository.dao.UserDao;
import com.exchange.core.util.KeyGenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {

    public static final String REDIS_USER_PREFIX = "current_user";
    private final Long REDIS_SAVE_USER_TIME = 86400L;

    private final UserDao userDao;
    private final EmailConfirmDao emailConfirmDao;
    private final CoinService coinService;
    private final WalletService walletService;
    private final OtpService otpService;
    private final MqPublisher mqPublisher;
    private final RedisTemplate redisTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder, CoinService coinService, WalletService walletService, OtpService otpService, EmailConfirmDao emailConfirmDao, MqPublisher mqPublisher, RedisTemplate redisTemplate) {
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.coinService = coinService;
        this.walletService = walletService;
        this.otpService = otpService;
        this.emailConfirmDao = emailConfirmDao;
        this.mqPublisher = mqPublisher;
        this.redisTemplate = redisTemplate;
    }

    public int findByEmailToCount(final String email) {
        return userDao.findByEmailToCount(email);
    }

    public User findOneByIdToAllFields(final Long id) {
        User user;
        final ValueOperations<String, User> operations = redisTemplate.opsForValue();
        final String key = REDIS_USER_PREFIX + id;

        log.info("----------findOneByIdToAllFields User key: {}", key);

        if (redisTemplate.hasKey(key)) {

            user = operations.get(key);
            if (user != null && user.getId() == id) {

                log.info("----------findOneByIdToAllFields cache: {}", user);

                return user;
            }
        }

        user = userDao.findOneByIdToAllFields(id);

        operations.set(key, user, REDIS_SAVE_USER_TIME, TimeUnit.SECONDS);//MINUTES分, MILLISECONDS毫秒单位, SECONDS秒

        return user;
    }

    public User findOneByEmailToAllFields(final String email) {
//        User user;
//        final ValueOperations<String, User> operations = redisTemplate.opsForValue();
//        final String key = DigestUtils.md5Hex(email);
//
//        log.info("----------findOneByEmailToAllFields User key: {}", key);
//
//        if (redisTemplate.hasKey(key)) {
//
//            user = operations.get(key);
//            if (user != null && user.getEmail().equals(email)) {
//
//                log.info("----------findOneByEmailToAllFields cache: {}", user);
//
//                return user;
//            }
//        }
//
//        user = userDao.findOneByEmailToAllFields(email);
//
//        operations.set(key, user, REDIS_SAVE_USER_TIME, TimeUnit.SECONDS);//MINUTES分, MILLISECONDS毫秒单位, SECONDS秒
//
//        return user;
        return userDao.findOneByEmailToAllFields(email);
    }

    @SoftTransational
    public CodeEnum sendEmailConfirm(final UserVo.ReqSignup reqSignup) {

        if (this.findByEmailToCount(reqSignup.getEmail()) > 0) {

            log.warn("邮箱已被注册: {}", reqSignup.getEmail());

            return CodeEnum.USER_EMAIL_EXIST;
        }

        User user = new User();
        user.setEmail(reqSignup.getEmail());
        user.setPwd(bCryptPasswordEncoder.encode(reqSignup.getPwd()));
        user.setLevel(LevelEnum.LEVEL1);
        //user.setOtpHash(otpService.createSecretKey());
        user.setActive(ActiveEnum.N);
        user.setRole(RoleEnum.USER);
        user.setRegDtm(LocalDateTime.now());
        userDao.insert(user);

        String code = KeyGenUtil.generateEmailConfirmNumericKey();
        EmailConfirm existEmailConfirm = emailConfirmDao.findOneByEmail(user.getEmail());
        EmailConfirm emailConfirm = new EmailConfirm();
        if (existEmailConfirm == null) {
            emailConfirm.setHashEmail(
                    KeyGenUtil.generateHashEmail(user.getId(), user.getPwd(), user.getEmail())
            );
            emailConfirm.setCode(code);
            emailConfirm.setEmail(user.getEmail());
            emailConfirm.setRegDtm(LocalDateTime.now());
            emailConfirm.setSendYn(ActiveEnum.N);
            emailConfirmDao.insert(emailConfirm);
        } else {
            existEmailConfirm.setCode(code);
            existEmailConfirm.setRegDtm(LocalDateTime.now());
            existEmailConfirm.setSendYn(ActiveEnum.N);
            emailConfirm = existEmailConfirm;
        }
        //email send publishing.
        EmailConfirm bindEmailConfirm = new EmailConfirm();
        bindEmailConfirm.setHashEmail(emailConfirm.getHashEmail());
        bindEmailConfirm.setCode(emailConfirm.getCode());
        bindEmailConfirm.setEmail(emailConfirm.getEmail());
        mqPublisher.signupEmailConfirm(bindEmailConfirm);

        return CodeEnum.USER_EMAIL_SENT;
    }

    @SoftTransational
    public EmailConfirmVo.ResEmailConfirm emailConfirm(final String hash, final String code) {

        EmailConfirm emailConfirm = emailConfirmDao.findOneByHashemailAndCode(hash, code);
        if (emailConfirm == null) {
            EmailConfirmVo.ResEmailConfirm resEmailConfirm = new EmailConfirmVo.ResEmailConfirm();
            resEmailConfirm.setTitle("邮箱验证失败.");
            resEmailConfirm.setMsg("验证失败. 请重新确认.");
            resEmailConfirm.setUrl("/signup");
            resEmailConfirm.setUrlTitle("注册用户");
            return resEmailConfirm;
        }

        if (ActiveEnum.Y.equals(emailConfirm.getSendYn())) {
            EmailConfirmVo.ResEmailConfirm resEmailConfirm = new EmailConfirmVo.ResEmailConfirm();
            resEmailConfirm.setTitle("邮箱验证失败.");
            resEmailConfirm.setMsg("无效的验证. 请重新确认.");
            resEmailConfirm.setUrl("/signup");
            resEmailConfirm.setUrlTitle("注册用户");
            return resEmailConfirm;
        }

        emailConfirm.setSendYn(ActiveEnum.Y);
        emailConfirmDao.updateSendYn(emailConfirm);

        User existUser = userDao.findOneByEmailToIdAndEmail(emailConfirm.getEmail());
        if (existUser == null) {
            EmailConfirmVo.ResEmailConfirm resEmailConfirm = new EmailConfirmVo.ResEmailConfirm();
            resEmailConfirm.setTitle("邮箱验证失败.");
            resEmailConfirm.setMsg("不存在的用户. 请重新确认.");
            resEmailConfirm.setUrl("/signup");
            resEmailConfirm.setUrlTitle("注册用户");
            return resEmailConfirm;
        }

        if (ActiveEnum.Y.equals(existUser.getActive())) {
            EmailConfirmVo.ResEmailConfirm resEmailConfirm = new EmailConfirmVo.ResEmailConfirm();
            resEmailConfirm.setTitle("邮箱验证失败.");
            resEmailConfirm.setMsg("已验证的用户. 请重新确认.");
            resEmailConfirm.setUrl("/sign");
            resEmailConfirm.setUrlTitle("登入");
            return resEmailConfirm;
        }

        existUser.setActive(ActiveEnum.Y);
        userDao.updateActive(existUser);

        EmailConfirmVo.ResEmailConfirm resEmailConfirm = new EmailConfirmVo.ResEmailConfirm();
        resEmailConfirm.setTitle("邮箱验证成功.");
        resEmailConfirm.setMsg("验证成功. 请登入开始交易.");
        resEmailConfirm.setUrl("/sign");
        resEmailConfirm.setUrlTitle("登入");

        //create wallet
        final List<Coin> coins = coinService.findAllToName();
        for (Coin coin : coins) {
            walletService.preCreateWallet(existUser.getId(), coin.getName());
        }

        return resEmailConfirm;
    }

    public UserVo.ResOtpAuth getRandomSecretKey(final String email) {
        final String secretKey = otpService.createSecretKey();
        UserVo.ResOtpAuth resOtpAuth = new UserVo.ResOtpAuth();
        resOtpAuth.setSecretKey(secretKey);
        resOtpAuth.setQRBarcodeURL(otpService.getQRBarcodeURL(email, secretKey));
        return resOtpAuth;
    }

    @SoftTransational
    public CodeEnum setUserSecretKey(final UserVo.CurrentUser currentUser, final String secretKey, final int authCode) {
        if (otpService.isOtpValid(secretKey, authCode)) {
            //更新GoogleAuth秘钥
            userDao.updateOtpHash(currentUser.getEmail(), secretKey);
            //删除Redis用户信息
            redisTemplate.delete(REDIS_USER_PREFIX + currentUser.getId());

            return CodeEnum.SUCCESS;
        } else {
            return CodeEnum.USER_OTP_AUTH_FAIL;
        }
    }

}
