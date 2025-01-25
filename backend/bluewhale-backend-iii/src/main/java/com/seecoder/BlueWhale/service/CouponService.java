package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.enums.GetCouponEnum;
import com.seecoder.BlueWhale.vo.CouponSetVO;
import com.seecoder.BlueWhale.vo.CouponVO;

import java.util.List;

public interface CouponService {
    Boolean create(CouponSetVO couponSet);
    List<Integer> getAllCouponSet();
    List<CouponVO> getAllCoupon(GetCouponEnum getCouponEnum);
    CouponVO acquire(Integer setId);
    Boolean check(Integer setId);
    CouponSetVO getCouponSetById(Integer setId);
    Boolean isValid(Integer setId);

}
