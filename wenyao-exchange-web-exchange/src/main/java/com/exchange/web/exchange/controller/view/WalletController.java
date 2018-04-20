package com.exchange.web.exchange.controller.view;

import com.exchange.core.domain.dto.User;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.UserService;
import com.exchange.core.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class WalletController {

    @Autowired private UserService userService;
    @Autowired private CoinService coinService;
    @Autowired private WalletService walletService;

    //http://127.0.0.1:8080/depositManage?coin=ETH
    @RequestMapping("/depositManage")
    public String depositManage(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                @RequestParam(value = "coin", required = false, defaultValue="BTC") String coinName , Model model) {
        final CoinEnum coinEnum = coinService.isCoin(coinName);
        if (coinEnum == null) {
            return "redirect:/";
        }

        final WalletVo.CoinList coins = walletService.getMyWalletsOfDeposit(currentUser.getId(), coinEnum);

        model.addAttribute("coinName", coinName);
        model.addAttribute("coins", coins.getCoins());
        model.addAttribute("currentWallet", coins.getCurrentWallet());

        return "depositManage";
    }

    @RequestMapping("/withdrawalManage")
    public String withdrawalManage(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                   @RequestParam(value = "coin", required = false, defaultValue="BTC") String coinName , Model model) {
        final CoinEnum coinEnum = coinService.isCoin(coinName);
        if (coinEnum == null) {
            return "redirect:/";
        }

        final User cacheUser = userService.findOneByIdToAllFields(currentUser.getId());

        final WalletVo.CoinList coins = walletService.getMyWalletsOfWithdrawal(cacheUser.getId(), cacheUser.getLevel(), coinEnum);

        model.addAttribute("coinName", coinName);
        model.addAttribute("coins", coins.getCoins());
        model.addAttribute("currentCoin", coins.getCurrentCoin());
        model.addAttribute("currentWallet", coins.getCurrentWallet());
        model.addAttribute("currentLevel", coins.getCurrentLevel());

        return "withdrawalManage";
    }

}
