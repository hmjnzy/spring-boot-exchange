package com.exchange.web.exchange.controller.view;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.TradeCoin;
import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.service.CoinService;
import com.exchange.core.service.TradeCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Slf4j
@Controller
public class TradeController {

    @Autowired private TradeCoinService tradeCoinService;
    @Autowired private CoinService coinService;
//    @Autowired private WalletService walletService;

    @RequestMapping("/trade")
    public String exchange(@ModelAttribute("user") UserVo.CurrentUser currentUser, @RequestParam(value = "coinPair") String coinPair, Model model) {

        CoinPairEnum pCoinPairEnum = tradeCoinService.isCoinPair(coinPair);
        if (pCoinPairEnum == null) {
            return "redirect:/";
        }

        final TradeCoin tradeCoin = tradeCoinService.findByCoinPairToActive(pCoinPairEnum);
        if (tradeCoin == null) {
            log.info("-------------TradeCoin was null {}", pCoinPairEnum);
            return "redirect:/";
        }

        if (ActiveEnum.N.equals(tradeCoin.getActive())) {
            //TODO 已下线币种
            log.info("-------------TradeCoin Active was N ");
            return "redirect:/";
        }

        final List<String> conins = tradeCoinService.splitCoinPair(pCoinPairEnum);

        final Coin firstBaseCoin = coinService.findOneByNameToAllFields(conins.get(0));
        if (firstBaseCoin == null) {
            log.info("-------------FirstBaseCoin was null {}", pCoinPairEnum);
            return "redirect:/";
        }

        final Coin secondTradeCoin = coinService.findOneByNameToAllFields(conins.get(1));
        if (secondTradeCoin == null) {
            log.info("-------------SecondTradeCoin was null {}", pCoinPairEnum);
            return "redirect:/";
        }

        model.addAttribute("coinPair", pCoinPairEnum.name());

//        if (currentUser != null) {
//
//            model.addAttribute("walletBaseCoin", walletService.findOneByUserIdAndCoin(currentUser.getId(), firstBaseCoin.getName()));
//            model.addAttribute("walletTradeCoin", walletService.findOneByUserIdAndCoin(currentUser.getId(), secondTradeCoin.getName()));
//        } else {
//            model.addAttribute("walletBaseCoin", null);
//            model.addAttribute("walletTradeCoin", null);
//        }

        model.addAttribute("fstBaseCoin", firstBaseCoin);
        model.addAttribute("secTradeCoin", secondTradeCoin);

        return "trade";
    }

}
