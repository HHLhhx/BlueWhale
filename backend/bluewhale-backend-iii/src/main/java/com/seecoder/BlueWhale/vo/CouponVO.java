package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.po.Coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponVO {
    Integer id;
    Integer setId;
    Integer uid;
    Boolean hasUsed;

    public Coupon toPO() {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setSetId(setId);
        coupon.setUid(uid);
        coupon.setHasUsed(hasUsed);
        return coupon;
    }
}
