package com.seecoder.BlueWhale.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResizableByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.enums.GetCouponEnum;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.repository.CouponRepository;
import com.seecoder.BlueWhale.repository.CouponSetRepository;
import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.CouponSetVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.vo.ResultVO;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    CouponService couponService;

    @PostMapping
    @Access(roles = {RoleEnum.STAFF, RoleEnum.CEO})
    public ResultVO<Boolean> create(@RequestBody CouponSetVO couponSet) {
        return ResultVO.buildSuccess(couponService.create(couponSet));
    }

    @GetMapping("/getSet")
    public ResultVO<List<Integer>> getAllCouponSet() {
        return ResultVO.buildSuccess(couponService.getAllCouponSet());
    }

    @GetMapping("/coupon/{getType}")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<List<CouponVO>> getAllCoupon(@PathVariable(value = "getType")GetCouponEnum getType) {
        return ResultVO.buildSuccess(couponService.getAllCoupon(getType));
    }

    @GetMapping("/{setId}/check")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Boolean> check(@PathVariable(value = "setId") Integer setId) {
        return ResultVO.buildSuccess(couponService.check(setId));
    }

    @GetMapping("/{setId}/get")
    public ResultVO<CouponSetVO> get(@PathVariable(value = "setId") Integer setId) { // 获取setId对应set的信息
        return ResultVO.buildSuccess(couponService.getCouponSetById(setId));
    }

    @GetMapping("/{setId}/acquire")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<CouponVO> acquire(@PathVariable(value = "setId") Integer setId) {
        return ResultVO.buildSuccess(couponService.acquire(setId));
    }

    @GetMapping("/{setId}/valid")
    public ResultVO<Boolean> isValid(@PathVariable(value = "setId") Integer setId) {
        return ResultVO.buildSuccess(couponService.isValid(setId));
    }
}
