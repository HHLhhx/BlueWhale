package com.seecoder.BlueWhale.controller;

import com.alibaba.excel.EasyExcel;
import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.util.OrderExcelItem;
import com.seecoder.BlueWhale.util.OssUtil;
import com.seecoder.BlueWhale.vo.CommentVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.vo.OrderVO;
import com.seecoder.BlueWhale.vo.ResultVO;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OssUtil ossUtil;

    @PostMapping
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Integer> create(@RequestBody OrderVO order) {
        return ResultVO.buildSuccess(orderService.create(order));
    }

    @GetMapping("/{id}")
    public ResultVO<OrderVO> getOrderById(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(orderService.getOrderById(id));
    }

    @PostMapping("/{id}/price")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Double> price(@PathVariable(value = "id") Integer id, @RequestBody List<Integer> couponList) {
        return ResultVO.buildSuccess(orderService.price(id, couponList));
    }

    @PostMapping("/{id}/validCouponList")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Boolean> validCouponList(@PathVariable(value = "id") Integer id, @RequestBody List<Integer> couponList) {
        return ResultVO.buildSuccess(orderService.validCouponList(id, couponList));
    }

    @PostMapping("/{id}/pay")
    @Access(roles = RoleEnum.CUSTOMER)
    public void pay(@PathVariable(value = "id") Integer id, @RequestParam(value = "isDirectPay") boolean isDirectPay,
                    @RequestBody List<Integer> couponList, HttpServletResponse httpServletResponse) {
        orderService.pay(id, isDirectPay, couponList, httpServletResponse);
    }

    @GetMapping
    public ResultVO<List<OrderVO>> getOrder() {
        return ResultVO.buildSuccess(orderService.getOrder());
    }

    @PostMapping("/{id}/delivery")
    @Access(roles = RoleEnum.STAFF)
    public ResultVO<Boolean> delivery(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(orderService.delivery(id));
    }

    @PostMapping("/{id}/receive")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Boolean> receive(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(orderService.receive(id));
    }

    // 特别的，该函数基于订单id来评论，是主要的评论方式
    @PostMapping("/{id}/comment")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<Boolean> comment(@PathVariable(value = "id") Integer id, @RequestBody CommentVO comment) {
        return ResultVO.buildSuccess(orderService.comment(id, comment));
    }

    @PostMapping("/{id}/coupon")
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<List<CouponVO>> availableCoupons(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(orderService.availableCoupons(id));
    }

    @GetMapping("/report")
    @Access(roles = {RoleEnum.STAFF, RoleEnum.CEO})
    public ResultVO<String> report() throws IOException {
        return ResultVO.buildSuccess(orderService.report());
    }
}