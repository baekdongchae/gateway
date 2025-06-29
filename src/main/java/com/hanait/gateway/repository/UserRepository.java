package com.hanait.gateway.repository;

import com.hanait.gateway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByUserCode(Long userCode);
}
