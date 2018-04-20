package com.exchange.core.domain.vo;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChartVo {
    private final String zero3 = "000";

    private String dt;
    private String open;
    private String high;
    private String low;
    private String close;

    public List<String> getRow() {
        List<String> row = new ArrayList<>();
        row.add(dt + zero3);
        row.add(open);
        row.add(high);
        row.add(low);
        row.add(close);
        return row;
    }

}
