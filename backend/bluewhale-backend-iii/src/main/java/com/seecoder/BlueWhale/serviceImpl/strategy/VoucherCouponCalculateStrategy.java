package com.seecoder.BlueWhale.serviceImpl.strategy;

import com.seecoder.BlueWhale.po.CouponSet;

public class VoucherCouponCalculateStrategy implements CalculateStrategy {
    public VoucherCouponCalculateStrategy(CouponSet couponSet) {
        reducePrice = couponSet.getReducePrice();
    }

    private final Double reducePrice;

    @Override
    public Double calculate(Double price) {
        return Math.max(0, price - reducePrice);
    }
}
