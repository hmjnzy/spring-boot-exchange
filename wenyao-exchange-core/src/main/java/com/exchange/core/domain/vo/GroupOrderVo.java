package com.exchange.core.domain.vo;

import com.exchange.core.util.DataUtil;
import java.math.BigDecimal;

public class GroupOrderVo {
    private String price;
    private String amount;
    private String totalPrice;

    public String getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = DataUtil.decimal(price);
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = DataUtil.decimal(amount);
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = DataUtil.decimal(totalPrice);
    }
}
