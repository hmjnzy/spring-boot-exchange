package com.exchange.core.provider.wallet;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.provider.feign.BlockchainInfoProvider;
import com.exchange.core.provider.wallet.proxy.BitcoinWalletRpcProxyProvider;
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
public class BitcoinWalletRpcProvider implements SimpleWalletRpcProvider {

    final BlockchainInfoProvider blockchainInfoProvider;
    final BitcoinWalletRpcProxyProvider bitcoinWalletRpcProxyProvider;
    final ModelMapper modelMapper;

    @Autowired
    public BitcoinWalletRpcProvider(BlockchainInfoProvider blockchainInfoProvider, BitcoinWalletRpcProxyProvider bitcoinWalletRpcProxyProvider, ModelMapper modelMapper) {
        this.blockchainInfoProvider = blockchainInfoProvider;
        this.bitcoinWalletRpcProxyProvider = bitcoinWalletRpcProxyProvider;
        this.modelMapper = modelMapper;
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal(0.0008); //0.00065269
    }

    //@Override
    public Coin getCoin() {
        Coin coin = new Coin();
        coin.setName(CoinEnum.BTC);
        return coin;
    }

    @Override
    public String getAccount(Long userId) {
        return SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId);
    }

//    @Override
//    public String getSendAddressFromTxId(String txId, WalletVo.TransactionInfo transactionInfo) {
//        //TODO 더 효율적으로 구하는 방법을 찾다
//        /*String address = "UNKNOWN";
//        try {
//            Map<String, Object> transaction = blockchainInfoProvider.getTransaction(txid);
//            for (Object input : ((List<Object>) transaction.get("inputs"))) {
//                Map<String, Object> rowInput = (Map<String, Object>) input;
//                Map<String, Object> prevOut = (Map<String, Object>) rowInput.get("prev_out");
//                if ((boolean) prevOut.get("spent") == true) {
//                    address = (String) prevOut.get("addr");
//                    return address;
//                }
//            }
//        } catch (Exception ex) {
//            log.error("getSendAddressFromTxId {}", ex.getMessage());
//        }
//        return address;*/
//        return transactionInfo.getAddress();
//    }

    @Override
    public List<WalletVo.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) {
        List<Map<String, Object>> transactions = bitcoinWalletRpcProxyProvider.listtransactions(
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
        return WalletVo.WalletCreateInfo.builder().address(
                bitcoinWalletRpcProxyProvider.getaccountaddress(getAccount(userId))
        ).build();
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        return new BigDecimal(String.valueOf(bitcoinWalletRpcProxyProvider.getbalance(getAccount(userId))));
    }

//    @Override
//    public WalletVo.TransactionInfo getTransaction(String txid) {
//        Map<String, Object> transaction = bitcoinWalletRpcProxyProvider.gettransaction(txid);
//        return modelMapper.map(transaction, WalletVo.TransactionInfo.class);
//    }

    @Override
    public String sendTo(Long sendUserId, String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception {
        return bitcoinWalletRpcProxyProvider.sendfrom(getAccount(sendUserId), toaddress, amount);
    }

    @Override
    public String sendFromHotWallet(String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception {
        return bitcoinWalletRpcProxyProvider.sendfrom(WalletType.HOT.name(), toaddress, amount);
    }
}
