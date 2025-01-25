package com.seecoder.BlueWhale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seecoder.BlueWhale.po.CouponSet;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponSetRepository extends JpaRepository<CouponSet, Integer> {
    @Query(value = "select id from coupon_set", nativeQuery = true)
    List<Integer> findAllId();

    @Query(value = "select id from coupon_set where store_id=?1", nativeQuery = true)
    List<Integer> findAllIdByStoreId(Integer storeId);

}
