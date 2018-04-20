package com.exchange.core.provider.wallet;

import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.exception.ExchangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletRpcProviderFactory {

//    final BitcoinWalletRpcProvider bitcoinRpcProvider;
//    final LitecoinWalletRpcProvider litecoinRpcProvider;
//    final DashWalletRpcProvider dashRpcProvider;
    private final EthereumWalletRpcProvider ethereumWalletRpcProvider;

    @Autowired
    public WalletRpcProviderFactory(EthereumWalletRpcProvider ethereumWalletRpcProvider) {
//        this.bitcoinRpcProvider = bitcoinRpcProvider;
//        this.litecoinRpcProvider = litecoinRpcProvider;
//        this.dashRpcProvider = dashRpcProvider;
        this.ethereumWalletRpcProvider = ethereumWalletRpcProvider;
    }

    public SimpleWalletRpcProvider get(CoinEnum coinEnum) {
        /*if (CoinEnum.BITCOIN.equals(coinEnum)) {
            return bitcoinRpcProvider;
        } else if (CoinEnum.LITECOIN.equals(coinEnum)) {
            return litecoinRpcProvider;
        } else if (CoinEnum.DASH.equals(coinEnum)) {
            return dashRpcProvider;
        } else */if (CoinEnum.ETH.equals(coinEnum)) {
            return this.ethereumWalletRpcProvider;
        } else {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
    }

}
