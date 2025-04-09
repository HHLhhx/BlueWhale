package com.seecoder.BlueWhale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.seecoder.BlueWhale.po.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByUserId(Integer userId);
    List<Order> findAllByStoreId(Integer storeId);

    @Query(value = "SELECT * FROM `order` as o WHERE o.order_state='UNPAID'", nativeQuery = true)
    List<Order> findAllUnpayOrder();
}
