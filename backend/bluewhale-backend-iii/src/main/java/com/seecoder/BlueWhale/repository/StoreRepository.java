package com.seecoder.BlueWhale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seecoder.BlueWhale.po.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    Store findByName(String name);

}
