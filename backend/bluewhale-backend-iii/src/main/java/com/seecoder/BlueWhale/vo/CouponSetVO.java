package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.CouponSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponSetVO {
    Integer id;
    CouponTypeEnum couponType;
    Double fulfillPrice;
    Double ratio;
    Double reducePrice;
    Date createTime;
    Date expireTime;
    Boolean isGlobal;
    Integer storeId;
    Integer totalNum;
    Integer sentNum;

    public CouponSet toPO() {
        CouponSet couponSet = new CouponSet();
        couponSet.setId(id);
        couponSet.setCouponType(couponType);
        couponSet.setFulfillPrice(fulfillPrice);
        couponSet.setReducePrice(reducePrice);
        couponSet.setCreateTime(createTime);
        couponSet.setExpireTime(expireTime);
        couponSet.setRatio(ratio);
        couponSet.setIsGlobal(isGlobal);
        couponSet.setStoreId(storeId);
        couponSet.setTotalNum(totalNum);
        couponSet.setSentNum(sentNum);
        return couponSet;
    }
}
