package com.seecoder.BlueWhale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seecoder.BlueWhale.po.Info;

public interface InfoRepository extends JpaRepository<Info, Integer> {
    List<Info> findAllByUid(Integer id);
}
