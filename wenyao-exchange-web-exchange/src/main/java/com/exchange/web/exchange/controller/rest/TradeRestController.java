package com.exchange.web.exchange.controller.rest;

import com.exchange.core.domain.enums.CodeEnum;
import com.exchange.core.domain.vo.*;
import com.exchange.core.service.MarketHistoryOrderService;
import com.exchange.core.service.MyHistoryOrderService;
import com.exchange.core.service.OrderService;
import com.exchange.core.service.TradeCompositeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/trade")
public class TradeRestController {

    @Autowired private TradeCompositeService tradeCompositeService;
    @Autowired private OrderService orderService;
    @Autowired private MarketHistoryOrderService marketHistoryOrderService;
    @Autowired private MyHistoryOrderService myHistoryOrderService;
    @Autowired private Validator validator;

    @PostMapping("/sell")
    public Response<TradeStatusVo> sell(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                        @Valid @RequestBody OrderVo.ReqOrder request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<TradeStatusVo>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        if (currentUser == null) {
            return Response.<TradeStatusVo>builder()
                    .status(CodeEnum.S403.getStatus())
                    .message(CodeEnum.S403.getMessage())
                    .build();
        }

        return Response.<TradeStatusVo>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(tradeCompositeService.sell(currentUser.getId(), request))
                .build();
    }

    @PostMapping("/buy")
    public Response<TradeStatusVo> buy(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                        @Valid @RequestBody OrderVo.ReqOrder request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<TradeStatusVo>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        if (currentUser == null) {
            return Response.<TradeStatusVo>builder()
                    .status(CodeEnum.S403.getStatus())
                    .message(CodeEnum.S403.getMessage())
                    .build();
        }

        return Response.<TradeStatusVo>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(tradeCompositeService.buy(currentUser.getId(), request))
                .build();
    }

    @PostMapping("/cancel")
    public Response<OrderVo.ResCancel> cancel(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                       @Valid @RequestBody OrderVo.ReqCancel request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<OrderVo.ResCancel>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        if (currentUser == null) {
            return Response.<OrderVo.ResCancel>builder()
                    .status(CodeEnum.S403.getStatus())
                    .message(CodeEnum.S403.getMessage())
                    .build();
        }

        return Response.<OrderVo.ResCancel>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(tradeCompositeService.cancel(currentUser.getId(), request.getOrderId()))
                .build();
    }

    @PostMapping("/getRealTimeOrders")
    public Response<OrderVo.ResRealTimeOrders> getRealTimeOrders(
                              @Valid @RequestBody OrderVo.ReqRealTimeOrders request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<OrderVo.ResRealTimeOrders>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        return Response.<OrderVo.ResRealTimeOrders>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(orderService.getRealTimeOrders(request))
                .build();
    }

    @RequestMapping("/getMarketHistoryOrders")
    public Response<OrderVo.ResMarketHistoryOrders> getMarketHistoryOrders(
                                    @Valid @RequestBody OrderVo.ReqMarketHistoryOrders request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return Response.<OrderVo.ResMarketHistoryOrders>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        return Response.<OrderVo.ResMarketHistoryOrders>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(marketHistoryOrderService.getMarketHistoryOrders(request))
                .build();
    }

    @RequestMapping("/getMyOrders")
    public Response<OrderVo.ResMyOrders> getMyOrders(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                                     @Valid @RequestBody OrderVo.ReqMyOrders request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors() || currentUser == null) {
            return Response.<OrderVo.ResMyOrders>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        return Response.<OrderVo.ResMyOrders>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(orderService.getMyOrders(currentUser.getId(), request))
                .build();
    }

    @RequestMapping("/getMyHistoryOrders")
    public Response<OrderVo.ResMyHistoryOrders> getMyHistoryOrders(@ModelAttribute("user") UserVo.CurrentUser currentUser,
                                @Valid @RequestBody OrderVo.ReqMyHistoryOrders request, BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors() || currentUser == null) {
            return Response.<OrderVo.ResMyHistoryOrders>builder()
                    .status(CodeEnum.FAIL.getStatus())
                    .message(CodeEnum.FAIL.getMessage())
                    .build();
        }

        return Response.<OrderVo.ResMyHistoryOrders>builder()
                .status(CodeEnum.SUCCESS.getStatus())
                .message(CodeEnum.SUCCESS.getMessage())
                .data(myHistoryOrderService.getMyHistoryOrders(currentUser.getId(), request))
                .build();
    }

}
