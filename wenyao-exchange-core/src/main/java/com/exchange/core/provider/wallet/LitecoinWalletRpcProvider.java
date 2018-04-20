package com.exchange.core.provider.wallet;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.provider.wallet.proxy.LitecoinWalletRpcProxyProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LitecoinWalletRpcProvider implements SimpleWalletRpcProvider {

    final LitecoinWalletRpcProxyProvider litecoinWalletRpcProxyProvider;
    final ModelMapper modelMapper;

    @Autowired
    public LitecoinWalletRpcProvider(LitecoinWalletRpcProxyProvider litecoinWalletRpcProxyProvider, ModelMapper modelMapper) {
        this.litecoinWalletRpcProxyProvider = litecoinWalletRpcProxyProvider;
        this.modelMapper = modelMapper;
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal(0.003);
    }

    //@Override
    public Coin getCoin() {
        Coin coin = new Coin();
        coin.setName(CoinEnum.LTC);
        return coin;
    }

    @Override
    public String getAccount(Long userId) {

        log.info("### /api/wallet/create 创建钱包 LitecoinWalletRpcProvider.createWallet.getAccount: ###{}",
                SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId));

        return SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId);
    }

//    @Override
//    public String getSendAddressFromTxId(String txId, WalletVo.TransactionInfo transactionInfo) {
//        return transactionInfo.getAddress();
//    }

    @Override
    public List<WalletVo.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) {
        List<Map<String, Object>> transactions = litecoinWalletRpcProxyProvider.listtransactions(
                "*"
                , page.getPageSize()
                , page.getPageNo()
        );

        List<WalletVo.TransactionInfo> transactionInfos = modelMapper.map(transactions, new TypeToken<List<WalletVo.TransactionInfo>>(){}.getType());
        return transactionInfos.stream().filter(r -> {
            if (categoryEnum.equals(r.getCategory())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public WalletVo.WalletCreateInfo createWallet(Long userId) {


        log.info("### /api/wallet/create 创建钱包 LitecoinWalletRpcProvider.createWallet.getAccount: ###{}",
                SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId));

        String address = litecoinWalletRpcProxyProvider.getaccountaddress(getAccount(userId));

        log.info("### /api/wallet/create 创建钱包 LitecoinWalletRpcProvider.createWallet address: ###{}", address);

        WalletVo.WalletCreateInfo xx = WalletVo.WalletCreateInfo.builder().address(
                address
        ).build();



        return xx;
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        return new BigDecimal(String.valueOf(litecoinWalletRpcProxyProvider.getbalance(getAccount(userId))));
    }

//    @Override
//    public WalletVo.TransactionInfo getTransaction(String txid) {
//        Map<String, Object> transaction = litecoinWalletRpcProxyProvider.gettransaction(txid);
//        return modelMapper.map(transaction, WalletVo.TransactionInfo.class);
//    }

    @Override
    public String sendTo(Long sendUserId, String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception {
        return litecoinWalletRpcProxyProvider.sendfrom(getAccount(sendUserId), toaddress, amount);
    }

    @Override
    public String sendFromHotWallet(String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception {
        return litecoinWalletRpcProxyProvider.sendfrom(WalletType.HOT.name(), toaddress, amount);
    }
}
