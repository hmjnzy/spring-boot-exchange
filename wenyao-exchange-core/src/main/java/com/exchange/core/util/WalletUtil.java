package com.exchange.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WalletUtil {
    public static BigDecimal scale(BigDecimal target) {
        return target.setScale(8, RoundingMode.DOWN);
    }
}
