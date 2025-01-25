package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.util.OrderExcelItem;
import com.seecoder.BlueWhale.vo.CommentVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.vo.OrderVO;
import com.seecoder.BlueWhale.vo.ResultVO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface OrderService extends AliPayable {
    public Integer create(OrderVO order);

    public void pay(Integer id, boolean isDirectPay, List<Integer> couponList, HttpServletResponse httpServletRequest);

    public List<OrderVO> getOrder();

    public Boolean delivery(Integer id);

    public Boolean receive(Integer id);

    public Boolean comment(Integer id, CommentVO comment);

    public OrderVO getOrderById(Integer id);

    public Double price(Integer id, List<Integer> couponList);

    public List<CouponVO> availableCoupons(Integer id);

    public List<OrderExcelItem> getReportData();

    public String report() throws UnsupportedEncodingException;

    public Boolean doClean();

    public Boolean validCouponList(Integer id, List<Integer> couponList);
}
