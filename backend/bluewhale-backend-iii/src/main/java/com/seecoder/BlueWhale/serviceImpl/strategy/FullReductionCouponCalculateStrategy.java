package com.seecoder.BlueWhale.serviceImpl.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.seecoder.BlueWhale.po.CouponSet;

public class FullReductionCouponCalculateStrategy implements CalculateStrategy {
    public FullReductionCouponCalculateStrategy(CouponSet couponSet) {
        fulfillPrice = couponSet.getFulfillPrice();
        reducePrice = couponSet.getReducePrice();
    }

    private final Double fulfillPrice;
    private final Double reducePrice;

    @Override
    public Double calculate(Double price) {
        if (price >= fulfillPrice)
            return new BigDecimal(price - reducePrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
