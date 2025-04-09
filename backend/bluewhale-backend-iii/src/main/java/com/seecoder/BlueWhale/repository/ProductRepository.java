package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByStoreId(Integer storeId);

    @Query(value = "select * from product where BINARY name = :name and store_id = :storeId", nativeQuery = true)
    Product findByStoreIdAndName(Integer storeId, String name);

    @Query(value = "select * from product where BINARY name = :name", nativeQuery = true)
    Product findByName(String name);
}
