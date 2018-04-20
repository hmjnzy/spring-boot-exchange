package com.exchange.core.provider.wallet;

import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import com.exchange.core.domain.vo.WalletVo;
import com.exchange.core.exception.ExchangeException;
import com.exchange.core.provider.wallet.proxy.EthereumWalletRpcProxyProvider;
import com.exchange.core.repository.dao.WalletDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Convert;
import rx.Observable;
import rx.observables.BlockingObservable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class EthereumWalletRpcProvider implements SimpleWalletRpcProvider {

    private final WalletDao walletDao;
    private final Web3j ethereumWalletWeb3jProxyProvider;
    private final EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider;

    @Autowired
    public EthereumWalletRpcProvider(EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider1, Web3j ethereumWalletWeb3jProxyProvider, WalletDao walletDao) {
        this.walletDao = walletDao;
        this.ethereumWalletWeb3jProxyProvider = ethereumWalletWeb3jProxyProvider;
        this.ethereumWalletRpcProxyProvider = ethereumWalletRpcProxyProvider1;
    }

    @Override
    public WalletVo.WalletCreateInfo createWallet(final Long userId) {
        String address = ethereumWalletRpcProxyProvider.personal_newAccount(this.getAccount(userId));
        return WalletVo.WalletCreateInfo.builder()
                .address(address)
                .tag("")
                .build();
    }

    @Override
    public String getAccount(final Long userId) {
        return ADDRESS_PREFIX + String.valueOf(userId);
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal("0.001");
    }

    private String _send(final String password, final String fromAddress, final String toaddress, final BigDecimal amount, final BigDecimal txFee)
            throws Exception {

        //ethereumWalletRpcProxyProvider.personal_unlockAccount(fromAddress, password, new BigInteger("3600"));
        //final BigInteger gasPrice = Convert.toWei(txFee, Convert.Unit.FINNEY).divideToIntegralValue(getMinFee()).toBigInteger();
        //txFee.multiply(FENNY_UNIT).divide(getMinFee()).toBigInteger();

        return ethereumWalletRpcProxyProvider.eth_sendTransaction(
                EthereumWalletRpcProxyProvider.EthTransaction.builder()
                        .from(fromAddress)
                        .to(toaddress)
                        .gas(String.format("0x%x", 21000))
                        .gasPrice(String.format("0x%x", 18))
                        .value(String.format("0x%x", Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()))
                        .data("0x")
                        .build()
        );
    }

    @Override
    public List<WalletVo.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) throws Exception {
        final BigInteger currentBlockNumber = ethereumWalletWeb3jProxyProvider.ethBlockNumber().send().getBlockNumber();
        final BigInteger allBlockNumber = currentBlockNumber;//currentBlockNumber.subtract(new BigInteger("7")); //5500컨펌의 기준으로 처리 (2일정도의 시간)
        final BigInteger startBlock = allBlockNumber.add(
                new BigInteger( String.valueOf(page.getPageNo() * page.getPageSize()) )
        );
        final BigInteger endBlock = startBlock.add(
                new BigInteger( String.valueOf((page.getPageNo() + 1) * page.getPageSize()) )
        );

        log.info("----------------------currentBlockNumber {}, startBlock {} endBlock {}", currentBlockNumber.toString(), startBlock, endBlock);
        //10 currentBlockNumber 7, startBlock(1) -3 startBlock(2) -3 startBlock(3) -1 ETH, Negative start index cannot be used
        //9 currentBlockNumber 7, startBlock(1) -3 startBlock(2) -3 startBlock(3) 0 ETH, Negative start index cannot be used
        //8currentBlockNumber 7, startBlock(1) -1 startBlock(2) -1 startBlock(3) 1 ETH, Negative start index cannot be used
        final Observable<EthBlock> ethBlockObservable = ethereumWalletWeb3jProxyProvider.replayBlocksObservable(
                DefaultBlockParameter.valueOf(startBlock ),/*>=0的整数*/
                DefaultBlockParameter.valueOf(endBlock ),/*>=1的整数*/
                true
        );

        final List<String> existWalletAddress = new ArrayList<>();
        final List<Wallet> existWallets = walletDao.findAllByCoinToAddress(CoinEnum.ETH);
        for (Wallet wallet : existWallets) {
            existWalletAddress.add(wallet.getAddress());
        }

        log.info("---------------------- existWalletAddress {}", existWalletAddress);

        final List<WalletVo.TransactionInfo> transactionInfos = new ArrayList<>();
        BlockingObservable.from(ethBlockObservable).subscribe(block -> {
            log.info("---------------------- block --------------{} ", block.getResult());
            if (block.getResult() != null) {

                final List<EthBlock.TransactionResult> transactions =  block.getResult().getTransactions();
                log.info("---------------------- transactions {}", transactions);

                for (EthBlock.TransactionResult transaction : transactions) {
                    final EthBlock.TransactionObject transactionHash = (EthBlock.TransactionObject)transaction.get();

                    log.info("--------???? getBlockNumber {}, amount {}, getTo {}, getHash {}",
                            transactionHash.getBlockNumber(),
                            Convert.fromWei(new BigDecimal(transactionHash.getValue()), Convert.Unit.ETHER),
                            transactionHash.getTo(),
                            transactionHash.getHash());

                    String toAddress = null;
                    CategoryEnum ethCategory = CategoryEnum.send;
                    for (final String address : existWalletAddress) {
                        if (address.equalsIgnoreCase(transactionHash.getTo())) {/*接收的地址匹配平台用户的ETH地址时Receive*/
                            toAddress = address;
                            ethCategory = CategoryEnum.receive;
                            break;
                        }
                    }

                    log.info("---------------------- ++++++++++++1+++++++++ {} , {}, {}", categoryEnum, ethCategory, existWalletAddress);

                    if (categoryEnum.equals(ethCategory)) {

                        log.info("---------------------- ++++++++++++2+++++++++平台用户钱包地址 {}", toAddress);

                        transactionInfos.add(
                                WalletVo.TransactionInfo.builder()
                                        .address(toAddress)
                                        .amount(Convert.fromWei(new BigDecimal(transactionHash.getValue()), Convert.Unit.ETHER))
                                        .category(ethCategory)
                                        .confirmations(currentBlockNumber.subtract(transactionHash.getBlockNumber()))
                                        //.timereceived(LocalDateTime.now())
                                        .txId(transactionHash.getHash())
                                        .build()
                        );
                    }
                }
            }
        });
        return transactionInfos;
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        final Wallet wallet = walletDao.findOneByUserIdAndCoin(userId, CoinEnum.ETH);
        if (wallet != null) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        final String _balance = ethereumWalletRpcProxyProvider.eth_getBalance(
                    wallet.getAddress(),
                    "latest"
                ).substring(2);

        final BigDecimal tmpBalance = new BigDecimal(new BigInteger(_balance, 16));
        final String bal = tmpBalance.toString();
        if ("0".equals(bal)) {
            return new BigDecimal("0.0");
        }

        final String k = String.format("%019.0f", tmpBalance);

        return new BigDecimal(k.substring(0, k.length() - 18) + "." + k.substring(k.length() - 18));
    }

    @Override
    public String sendFromHotWallet(final String fromAddress, final String toaddress, final BigDecimal amount, final BigDecimal txFee)
            throws Exception {
        return this._send(WalletType.HOT.name(), fromAddress, toaddress, amount, txFee);
    }

    @Override
    public String sendTo(final Long sendUserId, final String fromAddress, final String toaddress, final BigDecimal amount, final BigDecimal txFee)
            throws Exception {
        return _send(getAccount(sendUserId), fromAddress, toaddress, amount, txFee);
    }

//    @Override
//    public String getSendAddressFromTxId(String txId, WalletVo.TransactionInfo transactionInfo) {
//        return transactionInfo.getAddress();
//    }
//
//    @Override
//    public WalletVo.TransactionInfo getTransaction(String txid) {
//        return null;
//    }

}
