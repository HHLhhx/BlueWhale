package com.seecoder.BlueWhale.po;

import javax.persistence.*;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.util.BooleanToStringConverter;
import com.seecoder.BlueWhale.vo.CouponSetVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coupon_set")
public class CouponSet {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    Integer id;


    @Basic
    @Column(name = "coupon_type")
    @Enumerated(EnumType.STRING)
    CouponTypeEnum couponType;

    @Basic
    @Column(name = "fulfill_price")
    Double fulfillPrice;

    @Basic
    @Column(name = "reduce_price")
    Double reducePrice; // 满减券以及代金券会用到该功能

    @Basic
    @Column(name = "radio")
    Double ratio; // 直接

    @Basic
    @Column(name = "create_time")
    Date createTime;

    @Basic
    @Column(name = "expire_time")
    Date expireTime;

    @Basic
    @Column(name = "is_global")
    @Convert(converter = BooleanToStringConverter.class)
    Boolean isGlobal;

    @Basic
    @Column(name = "store_id")
    Integer storeId;

    @Basic
    @Column(name = "total_num")
    Integer totalNum;

    @Basic
    @Column(name = "sent_num")
    Integer sentNum;

    public CouponSetVO toVO() {
        CouponSetVO couponSet = new CouponSetVO();
        couponSet.setId(id);
        couponSet.setCouponType(couponType);
        couponSet.setFulfillPrice(fulfillPrice);
        couponSet.setReducePrice(reducePrice);
        couponSet.setRatio(ratio);
        couponSet.setCreateTime(createTime);
        couponSet.setExpireTime(expireTime);
        couponSet.setIsGlobal(isGlobal);
        couponSet.setStoreId(storeId);
        couponSet.setTotalNum(totalNum);
        couponSet.setSentNum(sentNum);
        return couponSet;
    }
}
