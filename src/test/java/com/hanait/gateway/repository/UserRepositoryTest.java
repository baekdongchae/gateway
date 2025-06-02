package com.hanait.gateway.repository;

import com.hanait.gateway.model.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

public interface UserRepositoryTest extends JpaRepository<UserTest, Long> {
}