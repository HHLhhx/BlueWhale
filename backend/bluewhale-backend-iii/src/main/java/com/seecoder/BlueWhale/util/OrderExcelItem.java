package com.seecoder.BlueWhale.util;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class OrderExcelItem {
    @ExcelProperty("购买者")
    String customerName;
    @ExcelProperty("交易时间")
    Date date;
    @ExcelProperty("交易商店")
    String storeName;
    @ExcelProperty("商品")
    String productName;
}
