package com.exchange.core.provider.wallet;

import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.vo.WalletVo;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

public interface SimpleWalletRpcProvider {

    /*
    * 内部创建钱包地址 前缀
    * */
    String ADDRESS_PREFIX = "v1_";

    WalletVo.WalletCreateInfo createWallet(Long userId);

    String getAccount(Long userId);

    BigDecimal getMinFee();

    List<WalletVo.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) throws Exception;

    BigDecimal getRealBalance(Long userId);

    /*
    * 提现
    * Admin Hot Wallet sendTo User Wallet(外部创建)
    * */
    String sendFromHotWallet(String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception;

    /*
    * 充值
    * User Wallet(内部创建) sendTo Admin Cold Wallet
    * */
    String sendTo(Long sendUserId, String fromAddress, String toaddress, BigDecimal amount, BigDecimal txFee) throws Exception;

//    String getSendAddressFromTxId(String txId, WalletVo.TransactionInfo transactionInfo);
//
//    WalletVo.TransactionInfo getTransaction(String txid);

    @Data
    class Page {
        private int pageNo;
        private int pageSize;
    }

}
