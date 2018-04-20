package com.exchange.core.domain.vo;

import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

public class UserVo {

    @Data
    public static class CurrentUser implements Serializable {
        private static final long serialVersionUID = -7210991847641111111L;
        private Long id;
        private String email;
    }

    @Data
    public static class ReqSignup {
        @Pattern(regexp="^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$",message="邮箱格式错误")
        private String email;
        @Pattern(regexp="(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}",message="密码必须是6~18位数字和字母的组合,并且首字母不能为数字")
        private String pwd;
    }

    @Data
    public static class ReqOtpAuth {
        @NotNull
        private String secretKey;
        @Pattern(regexp="[0-9]{6}",message="验证码,请输入6位数字")
        private int authCode;
    }

    @Data
    public static class ResOtpAuth {
        private String secretKey;
        private String qRBarcodeURL;
    }

/*
*     @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResActionLogs {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<ActionLog> actionLogs;
    }
* */
//    @Data
//    public static class ReqChangePassword {
//        @NotNull
//        private String password;
//        @NotNull
//        private String newPassword;
//        @NotNull
//        private String newPasswordRe;
//        @NotNull
//        private String otp;
//    }
//    @Data
//    public static class ReqActionLogs {
//        Integer pageNo;
//        Integer pageSize;//import com.exchange.core.domain.hibernate.ActionLog;
//    }
/*
    @Data
    public static class ResChangePassword {
        private Long id;
        private String email;
    }
    @Data
    public static class ReqReleaseMember {
    }
    @Data
    public static class ResReleaseMember {
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResActionLogs {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<ActionLog> actionLogs;
    }*/
}
