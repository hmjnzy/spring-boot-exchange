package com.exchange.core.domain.vo;

import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.exchange.core.domain.enums.StatusEnum;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionVo {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqTransactions {
        CategoryEnum category;
        CoinEnum coinName;
        StatusEnum status;
        Integer pageNo;
        Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqWithdrawal {
        @NotNull
        String coinName;
        @NotNull
        String address;
        String tag;
        @NotNull
        BigDecimal amount;
        @NotNull
        Integer authCode;
        //For Virtual Account Coin
        String bankNm;
        String recvNm;
    }

//    @Getter
//    @Setter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ResWithdrawal {
//        @NotNull
//        String coinName;
//        @NotNull
//        String address;
//        String tag;
//        BigDecimal amount;
//        //For Virtual Account Coin
//        String bankNm;
//        String recvNm;
//    }

    /*@Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResTransactions {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Transaction> transactions;
    }*/

}
