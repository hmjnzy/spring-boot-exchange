package com.exchange.web.exchange.controller.rest;

import com.exchange.core.domain.dto.ManualTransaction;
import com.exchange.core.domain.dto.Transaction;
import com.exchange.core.domain.enums.CategoryEnum;
import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.enums.StatusEnum;
import com.exchange.core.domain.vo.Response;
import com.exchange.core.domain.vo.TransactionVo;
import com.exchange.core.domain.vo.UserVo;
import com.exchange.core.service.TransactionService;
import com.exchange.core.util.KeyGenUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionRestController(TransactionService transactionService, ModelMapper modelMapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    /*
    * TODO
    * 目前还未判断 一次提现金额 和 可提现余额的 DB同步判断 只是依靠前端处理
    * */
    @PostMapping("/requestWithdrawal")
    public Response<String> requestWithdrawal(@ModelAttribute("user") UserVo.CurrentUser currentUser, @RequestBody TransactionVo.ReqWithdrawal request) {
        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        manualTransaction.setId(KeyGenUtil.generateTransactionId());
        manualTransaction.setUserId(currentUser.getId());
        manualTransaction.setCategory(CategoryEnum.send);
        manualTransaction.setRegDt(LocalDateTime.now());
        manualTransaction.setStatus(StatusEnum.PENDING);

        final CodeEnum codeEnum = transactionService.register(manualTransaction);

        return Response.<String>builder()
                .status(codeEnum.getStatus())
                .message(codeEnum.getMessage())
                //.data(modelMapper.map(transactionService.register(manualTransaction), TransactionVo.ResWithdrawal.class)) TransactionVo.ResWithdrawal
                .build();
    }

    @PostMapping("/getTransactions")
    public Response<PageInfo<Transaction>> getTransactions(@ModelAttribute("user") UserVo.CurrentUser currentUser, @RequestBody TransactionVo.ReqTransactions request) {
        return Response.<PageInfo<Transaction>>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(transactionService.getTransactions(currentUser.getId(), request))
                .build();
    }

}
