package com.exchange.core.service.batch;

import com.exchange.core.domain.dto.User;
import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.repository.dao.UserDao;
import com.exchange.core.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ResetWithdrawalLimitTotalBalanceBatchService {

    private final WalletService walletService;
    private final UserDao userDao;

    @Autowired
    public ResetWithdrawalLimitTotalBalanceBatchService(WalletService walletService, UserDao userDao) {
        this.walletService = walletService;
        this.userDao = userDao;
    }

    public void dailyReset() {

        final List<User> users = userDao.findAllToId();
        if (users == null) return;

        for (User user : users) {

            final List<Wallet> walletIds = walletService.findByUserIdAndTodayWithdrawalTotalBalanceGreaterThanToId(user.getId(), BigDecimal.ZERO);
            if (walletIds == null) continue;

            for (final Wallet wallet : walletIds) {
                wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
                walletService.updateByIdToTodayWithdrawalTotalBalance(wallet);
            }
        }
    }

}
