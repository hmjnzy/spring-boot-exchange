package com.exchange.core.repository.dao.impl;

import com.exchange.core.domain.dto.AdminWallet;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.WalletType;
import com.exchange.core.repository.dao.AdminWalletDao;
import com.exchange.core.repository.dao.BaseDao;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public class AdminWalletDaoImpl extends BaseDao implements AdminWalletDao {

    @Override
    public AdminWallet findOneByCoinAndTypeToAll(final CoinEnum coinEnum, final WalletType walletType) {
        final AdminWallet adminWallet = new AdminWallet();
        adminWallet.setCoinName(coinEnum);
        adminWallet.setType(walletType);
        return super.getSqlSession().selectOne(super.getStatement("findOneByCoinAndTypeToAll"), adminWallet);
    }

    @Override
    public AdminWallet findOneByCoinAndTypeToAddressAndAvailableBalance(final CoinEnum coinEnum, final WalletType walletType) {
        final AdminWallet adminWallet = new AdminWallet();
        adminWallet.setCoinName(coinEnum);
        adminWallet.setType(walletType);
        return super.getSqlSession().selectOne(super.getStatement("findOneByCoinAndTypeToAddressAndAvailableBalance"), adminWallet);
    }

    @Override
    public int updateByCoinAndTypeToAvailableBalance(final CoinEnum coinEnum, final WalletType walletType, final BigDecimal availableBalance) {
        final AdminWallet adminWallet = new AdminWallet();
        adminWallet.setCoinName(coinEnum);
        adminWallet.setType(walletType);
        adminWallet.setAvailableBalance(availableBalance);
        return super.getSqlSession().update(super.getStatement("updateByCoinAndTypeToAvailableBalance"), adminWallet);
    }

}
