package com.exchange.core.util;

import com.baomidou.mybatisplus.toolkit.IdWorker;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import java.time.LocalDateTime;
import java.util.Random;

public class KeyGenUtil {

    static int min = 10;
    static int max = 99;

    public static String generateTransactionId() {
        return DateUtil.FORMATTER_YYMMDD.format(LocalDateTime.now()) + String.valueOf(IdWorker.getId()) + (new Random().nextInt(max) % (max - min + 1) + min); /*26位 如:18012022954720955760914433*/
    }

    public static String generateEmailConfirmNumericKey() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public static String generateHashEmail(Long userId, String pwd, String email) {
        return DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(32) + "_" + userId + "_" + pwd + "_" + email + "_" + RandomStringUtils.randomAlphanumeric(32));
    }

//    public static String generateDocFileName() {
//        return RandomStringUtils.randomAlphanumeric(64);
//    }
//
//    public static String generateApiKey() {
//        return RandomStringUtils.randomAlphanumeric(32);
//    }
//
//    public static String generateTxId() {
//        return RandomStringUtils.randomAlphanumeric(64);
//    }
//
//    public static String generateKey(int length) {
//        return RandomStringUtils.randomAlphanumeric(length);
//    }
//
//    public static String generateNumericKey(int length) {
//        return RandomStringUtils.randomNumeric(length);
//    }

}
