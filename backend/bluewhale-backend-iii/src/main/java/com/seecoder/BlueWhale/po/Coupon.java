package com.seecoder.BlueWhale.po;

import javax.persistence.*;

import com.seecoder.BlueWhale.util.BooleanToStringConverter;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.util.BooleanToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Coupon {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    Integer id;

    @Basic
    @Column(name = "set_id")
    Integer setId;

    @Basic
    @Column(name = "uid")
    Integer uid;

    @Basic
    @Column(name = "has_used")
    @Convert(converter = BooleanToStringConverter.class)
    Boolean hasUsed;

    public CouponVO toVO() {
        CouponVO coupon = new CouponVO();
        coupon.setId(id);
        coupon.setSetId(setId);
        coupon.setUid(uid);
        coupon.setHasUsed(hasUsed);
        return coupon;
    }
}
