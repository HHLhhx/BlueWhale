package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import com.seecoder.BlueWhale.po.Coupon;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    public Coupon findBySetIdAndUid(Integer setId, Integer uid);

    List<Coupon> findAllByUid(Integer uid);

    List<Coupon> findAllByUidAndSetId(Integer uid, Integer setId);
}
