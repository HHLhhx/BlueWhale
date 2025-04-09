package com.seecoder.BlueWhale.exception;

public class BlueWhaleException extends RuntimeException {

    public BlueWhaleException(String message) {
        super(message);
    }

    public static BlueWhaleException phoneAlreadyExists() {
        return new BlueWhaleException("手机号已经存在!");
    }

    public static BlueWhaleException notLogin() {
        return new BlueWhaleException("未登录!");
    }

    public static BlueWhaleException phoneOrPasswordError() {
        return new BlueWhaleException("手机号或密码错误!");
    }

    public static BlueWhaleException fileUploadFail() {
        return new BlueWhaleException("文件上传失败!");
    }

    public static BlueWhaleException nameAlreadyExists() {
        return new BlueWhaleException("名称已经存在!");
    }

    public static BlueWhaleException storeNotExists() {
        return new BlueWhaleException("店铺不存在!");
    }

    public static BlueWhaleException productNotExists() {
        return new BlueWhaleException("商品不存在!");
    }

    public static BlueWhaleException productLackStock() {
        return new BlueWhaleException("商品库存不足!");
    }

    public static BlueWhaleException orderNotExist() {
        return new BlueWhaleException("订单不存在!");
    }

    public static BlueWhaleException couponNotExist() {
        return new BlueWhaleException("优惠券不存在!");
    }

    public static BlueWhaleException couponSetNotExist() {
        return new BlueWhaleException("优惠券组不存在!");
    }

    public static BlueWhaleException couponSetAllSent() {
        return new BlueWhaleException("优惠券组已经发放完了!");
    }

    public static BlueWhaleException holdCouponAlready() {
        return new BlueWhaleException("不能多次领取同一个优惠券组的优惠券!");
    }

    public static BlueWhaleException couponHasUsed() {
        return new BlueWhaleException("优惠券已经被使用!");
    }

    public static BlueWhaleException illegalUserAccess() {
        return new BlueWhaleException("非法的用户访问!");
    }

    public static BlueWhaleException illegalOrderState() {
        return new BlueWhaleException("错误的订单状态!");
    }

    public static BlueWhaleException userNotExist() {
        return new BlueWhaleException("用户不存在");
    }

    public static BlueWhaleException payError() {
        return new BlueWhaleException("支付失败！");
    }

    public static Exception commentNotExist() {
        return new BlueWhaleException("评论不存在！");
    }

    public static BlueWhaleException acquireCouponFailed() {
        return new BlueWhaleException("领取优惠券失败！");
    }
}
