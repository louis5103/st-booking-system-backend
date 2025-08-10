package com.springproject.stbookingsystem.repository;


import com.springproject.stbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 찾기
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호로 사용자 찾기
     */
    Optional<User> findByPhone(String phone);

    /**
     * 전화번호 존재 여부 확인
     */
    boolean existsByPhone(String phone);
}