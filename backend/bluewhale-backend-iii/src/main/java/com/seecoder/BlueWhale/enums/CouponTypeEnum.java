package com.seecoder.BlueWhale.enums;

public enum CouponTypeEnum {
    FULL_REDUCTION, // 满减券
    VOUCHER, // 代金券，减免金额对应reducePrice
    DIRECT_DISCOUNT, // 直接打折券，打折比例对应couponset的ratio
    SPECIAL, // 蓝鲸券
}
