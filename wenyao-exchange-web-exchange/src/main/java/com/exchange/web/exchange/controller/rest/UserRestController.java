package com.exchange.web.exchange.controller.rest;

import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.vo.Response;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserRestController {

    @Autowired private UserService userService;
    @Autowired private Validator validator;

    @PostMapping("/doSignup")
    public Response<String> doRegist(@ModelAttribute UserVo.ReqSignup reqSignup, BindingResult bindingResult) {
    	validator.validate(reqSignup, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<String>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }
        final CodeEnum codeEnum = userService.sendEmailConfirm(reqSignup);
        return Response.<String>builder()
                .status(codeEnum.getStatus())
                .message(codeEnum.getMessage())
                .build();
    }

    @GetMapping("/otpAuth")
    public Response<UserVo.ResOtpAuth> otpAuth(@ModelAttribute("user") UserVo.CurrentUser currentUser) {
        return Response.<UserVo.ResOtpAuth>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(userService.getRandomSecretKey(currentUser.getEmail()))
                .build();
    }

    @PostMapping("/otpAuth")
    public Response<String> otpAuth(@ModelAttribute("user") UserVo.CurrentUser currentUser, @ModelAttribute UserVo.ReqOtpAuth reqOtpAuth) {
        final CodeEnum codeEnum = userService.setUserSecretKey(currentUser, reqOtpAuth.getSecretKey(), reqOtpAuth.getAuthCode());
        return Response.<String>builder()
                .status(codeEnum.getStatus())
                .message(codeEnum.getMessage())
                .build();
    }

}
