package com.exchange.core.domain.vo;

import lombok.Data;

public class EmailConfirmVo {

    @Data
    public static class ResEmailConfirm {
        private String title;
        private String msg;
        private String url;
        private String urlTitle;
    }
}
