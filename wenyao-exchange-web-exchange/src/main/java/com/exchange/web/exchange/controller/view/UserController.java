package com.exchange.web.exchange.controller.view;

import com.exchange.core.domain.dto.User;
import com.exchange.core.domain.vo.EmailConfirmVo;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.UserService;
import com.exchange.core.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private CoinService coinService;
    @Autowired private WalletService walletService;

    @RequestMapping("/dashboard")
    public String dashboard() {

        log.info("-------------dashboard:");

        return "dashboard";
    }

    @RequestMapping("/security")
    public String security(@ModelAttribute("user") UserVo.CurrentUser currentUser, Model model) {

        final User cacheUser = userService.findOneByIdToAllFields(currentUser.getId());

        log.info("-------------security: CurrentUser {}, User {}", currentUser, cacheUser);

        model.addAttribute("otpHash", cacheUser.getOtpHash());

        if (cacheUser.getOtpHash() != null) {

        }

        return "security";
    }

    @RequestMapping("/sign")
    public String sign() {

        //UserVo;

        log.info("-------------sign:");

        return "sign";
    }

    @RequestMapping("/signup")
    public String signup() {

        log.info("-------------signup:");

        return "signup";
    }

    @RequestMapping("/signupEmail")
    public String signupEmail() {

        log.info("-------------signupEmail:");

        return "email/signup";
    }

    @RequestMapping(value = "/emailConfirm", method = RequestMethod.GET)
    public String emailConfirm(@RequestParam("hash") String hash, @RequestParam("code") String code, Model model) {

        EmailConfirmVo.ResEmailConfirm resEmailConfirm = userService.emailConfirm(hash, code);

        log.info("-------------emailConfirm resEmailConfirm: {}", resEmailConfirm);

        model.addAttribute("ResEmailConfirm", resEmailConfirm);//${confirm_url}

        return "emailConfirm";
    }

}
