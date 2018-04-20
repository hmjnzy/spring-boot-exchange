package com.exchange.core.service;

import com.exchange.core.annotation.HardTransational;
import com.exchange.core.domain.dto.MyHistoryOrder;
import com.exchange.core.domain.vo.OrderVo;
import com.exchange.core.repository.dao.MyHistoryOrderDao;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class MyHistoryOrderService {

    private final MyHistoryOrderDao myHistoryOrderDao;

    @Autowired
    public MyHistoryOrderService(MyHistoryOrderDao myHistoryOrderDao) {
        this.myHistoryOrderDao = myHistoryOrderDao;
    }

    @HardTransational
    public int insert(final MyHistoryOrder myHistoryOrder) {
        return myHistoryOrderDao.insert(myHistoryOrder);
    }

    public OrderVo.ResMyHistoryOrders getMyHistoryOrders(final Long userId, final OrderVo.ReqMyHistoryOrders request) {
        final List<MyHistoryOrder> myHistoryOrdersPage = myHistoryOrderDao.findAllByUserIdAndCoinPairOrderById(
                userId,
                request.getCoinPair(),
                request.getPageNo(),
                20
        );
        final PageInfo<MyHistoryOrder> orderPageInfo = new PageInfo<>(myHistoryOrdersPage);
        return OrderVo.ResMyHistoryOrders.builder()
                .pageNum(orderPageInfo.getPageNum())
                .pageSize(orderPageInfo.getPageSize())
                .size(orderPageInfo.getSize())
                .startRow(orderPageInfo.getStartRow())
                .endRow(orderPageInfo.getEndRow())
                .total(orderPageInfo.getTotal())
                .pages(orderPageInfo.getPages())
                .list(orderPageInfo.getList())
                .build();
    }

}
