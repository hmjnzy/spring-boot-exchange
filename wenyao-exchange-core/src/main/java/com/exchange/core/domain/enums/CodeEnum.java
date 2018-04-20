package com.exchange.core.domain.enums;

public enum CodeEnum {
    SUCCESS(0, "success"),
    FAIL(1, "fail"),
    BAD_REQUEST(2, "bad request"),

    S403(403, "403 Forbidden"),

    COINPAIR_NOT_EXIST(1000, "coinPair not exist"),

    WALLET_ALREADY_EXIST(2000, "wallet already exist"),
    WALLET_NOT_EXIST(2001, "wallet not exist"),
    AVAILABLE_BALANCE_NOT_ENOUGH(2002, "available balance not enough"),
    AMOUNT_IS_UNDER_MIN_AMOUNT(2003, "amount is under min amount"),
    ALREADY_TRANSACTION_EXIST(2004, "already transaction exist"),
    ALREADY_MANUAL_TRANSACTION_EXIST(2005, "already manual transaction exist"),

    AMOUNT_IS_UNDER_ZERO(3000, "amount is under 0"),

    INVAILD_ORDER_TYPE(4000, "order type error"),
    PRICE_CANT_ZERO_OR_UNDER_ZERO(4001, "price can`t zero or under zero"),
    AMOUNT_CANT_ZERO_OR_UNDER_ZERO(4002, "amount can`t zero or under zero"),
    COIN_NOT_ACTIVE(4003, "coin not active"),
    COINPAIR_NOT_ACTIVE(4004, "coinpair not active"),
    COIN_INSUFFICIENT_BALANCE(4005, "coin insufficient balance"),
    INVALID_ORDER_TYPE(4006, "invalid order type"),
    ORDER_CANCEL_FAIL(4007, "order cancel fail"),

    USER_EMAIL_EXIST(7000, "邮箱已被注册"),
    USER_EMAIL_SENT(7001, "邮箱已发送"),
    USER_OTP_AUTH_FAIL(7002, "验证失败, 验证码不正确或过期")



    ;
    /*SUCCESS("0000", "success"),
    FAIL("1000", "fail"),
    UNKNOWN_ERROR("1001", "unknown error"),

    CONSTANT_VALUE_IS_NULL("1003", "constant value is null"),

    USER_NOT_EXIST("4001", "user not exist"),
    USER_SETTING_NOT_EXIST("4002", "user setting not exist"),
    API_KEY_INVALID("4003", "api key invalid"),



    ORDER_TYPE_INVALID("4007", "OrderType invalid."),
    NOT_ENOUGH_BALANCE("4008", "avaliable balance not enough"),
    INVALID_CONFIRM_CODE("4009", "invalid confirm code"),
    INVALID_EMAIL("4010", "invalid email"),


    ORDER_NOT_EXIST("4013", "order not exist"),
    ORDER_STATUS_IS_NOT_PLACED("4014", "order status is not placed"),
    MIN_AMOUNT("4015", "order amount is under min amount"),
    NOT_SUPPORTED("4016", "not supported"),
    USER_FDS_LOCK("4017", "user fds lock"),
    ADMIN_WALLET_BALANCE_IS_UNDER_ZERO("4018", "admin wallet balance is under zero"),
    WALLET_UNLOCK_IS_FAIL("4019", "wallet unlock is fail"),
    DO_NOT_ALLOW_INNER_TRANSFER_WALLET("4020", "do not allow inner transfer wallet"),

    ALREADY_SEND_PROCESS_RUNNING("4022", "already send process is running"),
    MANUAL_TRANSACTION_NOT_EXIST("4023", "manual transaction not exist"),
    ADMIN_WALLET_NOT_EXIST("4023", "admin wallet not exist"),
    ONLY_KRW_RECEIVED_REQUEST("4024", "only krw received request"),
    ONLY_KRW_SEND_REQUEST("4025", "only krw send request"),
    ALREADY_STATUS_IS_NOT_PENDING("4026", "already status is not pending"),
    NOT_ENOUGH_VIRTUAL_ACCOUNT("4027", "not enough vitrual account"),


    TRANSACTION_NOT_EXIST("4030", "transaction not exist"),

    INVALID_PASSWORD("4032", "Invalid password"),




    ALREADY("501", "already"),
    EQUAL_USER("502", "equal_user");*/


    private int status;
    private String message;

    CodeEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
