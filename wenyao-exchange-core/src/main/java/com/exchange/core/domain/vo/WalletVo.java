package com.exchange.core.domain.vo;

import com.exchange.core.domain.dto.Coin;
import com.exchange.core.domain.dto.Level;
import com.exchange.core.domain.dto.Wallet;
import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CoinEnum;
import com.sun.istack.internal.Nullable;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class WalletVo {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResCreateWallet {
        @NotNull
        String address;
        @Nullable
        String tag;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoinList {
        private List<Coins> coins;
        private Coin currentCoin;
        private Wallet currentWallet;
        private Level currentLevel;
        @ToString
        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Coins {
            private Coin coin;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletInfos {
        private List<Info> infos;
        @ToString
        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Info {
            private Coin coin;
            private Wallet wallet;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletCreateInfo {
        @NotNull
        String address;
        String tag;
        //For Virtual Account
        String bankName;
        String bankCode;
        String recvCorpNm;
    }

    @Data
    public static class ReqCoinPair {
        @NotNull
        String coinPair;
    }

    @Data
    public static class ReqCreateWallet {
        @NotNull
        String coinName;
    }

    @ToString
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionInfo implements Serializable {
        private static final long serialVersionUID = 8560063755242222222L;
        String id;
        Long userId;
        CoinEnum coinName;
        String address;
        CategoryEnum category;
        BigDecimal amount;
        BigInteger confirmations;
        String txId;
        String bankNm;
        String recvNm;
        LocalDateTime timereceived;
    }
   /* @Data
    public static class ResWallet {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Wallet> contents;
    }*/
   /* @Data
    public static class ReqWallet {
        Integer pageNo;
        Integer pageSize;
    }
    @Data
    public static class ReqAdd {

    }
    @Data
    public static class ReqEdit {
    }
    @Data
    public static class ReqDel {

    }
*/


}
