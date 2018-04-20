package com.exchange.core.repository.dao;

import com.exchange.core.domain.dto.ManualTransaction;
import com.exchange.core.domain.enums.StatusEnum;
import java.time.LocalDateTime;

public interface ManualTransactionDao {
    ManualTransaction findOneByIdAndUserIdToAllFields(ManualTransaction manualTransaction);
    ManualTransaction findByIdAndUserIdToRegDt(String id);
    int insert(ManualTransaction manualTransaction);
    int findByIdAndUserIdToCount(ManualTransaction manualTransaction);
    int updateCompleteStatus(String id, Long userId, StatusEnum status, LocalDateTime regDt);
}
