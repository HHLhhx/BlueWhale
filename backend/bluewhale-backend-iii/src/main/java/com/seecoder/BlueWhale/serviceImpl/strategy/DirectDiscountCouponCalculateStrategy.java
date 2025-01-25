package com.seecoder.BlueWhale.serviceImpl.strategy;

import com.seecoder.BlueWhale.po.CouponSet;

public class DirectDiscountCouponCalculateStrategy implements CalculateStrategy {
    public DirectDiscountCouponCalculateStrategy(CouponSet couponSet) {
        ratio = couponSet.getRatio();
    }

    private final Double ratio;

    @Override
    public Double calculate(Double price) {
        return price * ratio;
    }
}
