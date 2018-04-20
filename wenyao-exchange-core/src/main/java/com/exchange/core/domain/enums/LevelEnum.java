package com.exchange.core.domain.enums;

public enum LevelEnum {

    LEVEL1("LEVEL1", "第一阶段认证会员"),
    LEVEL2("LEVEL2", "第二阶段认证会员"),
    LEVEL3("LEVEL3", "第三阶段认证会员");

    private String code;
    private String desc;

    LevelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
