package com.exchange.web.exchange.controller.rest;

import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.CoinPairEnum;
import com.exchange.core.domain.vo.Response;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.service.TradeCoinService;
import com.exchange.core.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/wallet")
public class WalletRestController {

    @Autowired private WalletService walletService;
    @Autowired private TradeCoinService tradeCoinService;

    @RequestMapping(value = "/getMyWalletsTradeCoin", method = RequestMethod.POST)
    public Response<WalletVo.WalletInfos> getMyWalletsTradeCoin(@ModelAttribute("user") UserVo.CurrentUser currentUser, @Valid @RequestBody WalletVo.ReqCoinPair request) {

        CoinPairEnum coinPairEnum = tradeCoinService.isCoinPair(request.getCoinPair());
        if (coinPairEnum == null) {
            return Response.<WalletVo.WalletInfos>builder()
                    .status(CodeEnum.COINPAIR_NOT_EXIST.getStatus())
                    .message(CodeEnum.COINPAIR_NOT_EXIST.getMessage())
                    .data(null)
                    .build();
        }

        return Response.<WalletVo.WalletInfos>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(walletService.getMyWalletsTradeCoin((currentUser == null ? 0L : currentUser.getId()), coinPairEnum))
                .build();
    }

    @PostMapping("/create")
    public Response<WalletVo.ResCreateWallet> create(@ModelAttribute("user") UserVo.CurrentUser currentUser, @Valid @RequestBody WalletVo.ReqCreateWallet request) {
        return Response.<WalletVo.ResCreateWallet>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(walletService.createWallet(currentUser.getId(), CoinEnum.valueOf(request.getCoinName())))
                .build();
    }

}
