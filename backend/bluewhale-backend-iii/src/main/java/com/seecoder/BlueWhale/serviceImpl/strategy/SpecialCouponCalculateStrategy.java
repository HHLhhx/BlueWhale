package com.seecoder.BlueWhale.serviceImpl.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author: DingXiaoyu
 * @Date: 22:35 2023/12/19
 * “蓝鲸券”使用规则：
 * 0-100元区间打九五折；
 * 100-200元区间打九折；
 * 200-300元区间打八五折；
 * 300-400元区间打八折；
 * 400-500元区间打七五折；
 * 500元以上区间打七折。
 */
public class SpecialCouponCalculateStrategy implements CalculateStrategy {

    // every entry is (a, b, c) pair in formula "a(x - c) + b"
    static final Double numPerLevel = 100.0;
    static final Double[][] calcTable = new Double[][]{
            {0.95, 0.0, 0.0},
            {0.90, 95.0, 100.0},
            {0.85, 185.0, 200.0},
            {0.80, 270.0, 300.0},
            {0.75, 350.0, 400.0},
            {0.70, 425.0, 500.0},
    };

    @Override
    public Double calculate(Double price) {
        double result = 0.0;
        Double[] calcEntry = calcTable[((int) (price / 100.0))];
        result = calcEntry[0] * (price - calcEntry[2]) + calcEntry[1];
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
