package com.exchange.core.util;

import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class CalculateFeeUtil {
    /*
    * @Param amount为总金额
    * @Param 交易费率%
    * 交易总金额 减去(-) 费率(%) = 实际用户拿到金额
    * */
    public BigDecimal getRealAmount(final BigDecimal amount, final BigDecimal feePercent) {
        return WalletUtil.scale(
                amount.subtract(getFeeAmount(amount, feePercent))
        );
    }
    //算出 费率
    public BigDecimal getFeeAmount(final BigDecimal amount, final BigDecimal feePercent) {
        return WalletUtil.scale(
                feePercent.divide(new BigDecimal("100")).multiply(amount)
        );
    }

}