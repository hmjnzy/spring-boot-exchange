package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.AdminWallet;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import java.math.BigDecimal;

public interface AdminWalletDao {
    AdminWallet findOneByCoinAndTypeToAll(CoinEnum coinEnum, WalletType walletType);
    AdminWallet findOneByCoinAndTypeToAddressAndAvailableBalance(CoinEnum coinEnum, WalletType walletType);
    int updateByCoinAndTypeToAvailableBalance(CoinEnum coinEnum, WalletType walletType, BigDecimal availableBalance);
}
