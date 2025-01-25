package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phone);

    void deleteByPhone(String phone);

    User findByPhoneAndPassword(String phone, String password);
}
