package com.hanait.gateway.model;

import com.hanait.gateway.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class BaseTimeEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUpdateTimeChangesOnEntityUpdate() throws Exception {
        // Given
        User user = User.builder()
                .userId("test01")
                .userPw("1234")
                .role(Role.ROLE_USER)
                .build();

        // When - Save the user
        User savedUser = userRepository.save(user);
        
        // Then - Check if createTime and updateTime were set
        assertNotNull(savedUser.getCreateTime());
        assertNotNull(savedUser.getUpdateTime());
        
        // Record initial times
        LocalDateTime initialCreateTime = savedUser.getCreateTime();
        LocalDateTime initialUpdateTime = savedUser.getUpdateTime();
        
        log.info("Initial createTime: {}", initialCreateTime);
        log.info("Initial updateTime: {}", initialUpdateTime);
        
        // Ensure the times are the same or very close when first created
        long diffBetweenCreateAndUpdate = ChronoUnit.MILLIS.between(initialCreateTime, initialUpdateTime);
        assertTrue(Math.abs(diffBetweenCreateAndUpdate) < 100, 
                "Create time and update time should be very close when entity is first created");
        
        // Sleep a short time to ensure timestamps will be different
        Thread.sleep(100);
        
        // When - Update the user
        savedUser.setUserPw("updated_password");
        User updatedUser = userRepository.save(savedUser);
        
        // Then - Check if updateTime was updated but createTime remains the same
        assertEquals(initialCreateTime, updatedUser.getCreateTime(), 
                "Create time should not change after update");
        
        assertNotEquals(initialUpdateTime, updatedUser.getUpdateTime(), 
                "Update time should change after update");
        
        assertTrue(updatedUser.getUpdateTime().isAfter(initialUpdateTime), 
                "New update time should be after the initial update time");
        
        log.info("After update - createTime: {}", updatedUser.getCreateTime());
        log.info("After update - updateTime: {}", updatedUser.getUpdateTime());
    }

//    @Test
//    void testMultipleUpdatesChangeUpdateTime() throws Exception {
//        // Given
//        User user = User.builder()
//                .userId("test02")
//                .userPw("1234")
//                .role(Role.ROLE_USER)
//                .build();
//
//        // When - Initial save
//        User savedUser = userRepository.save(user);
//        LocalDateTime firstUpdateTime = savedUser.getUpdateTime();
//
//        // Sleep to ensure timestamp difference
//        Thread.sleep(100);
//
//        // First update
//        savedUser.setUserPw("password2");
//        User firstUpdate = userRepository.save(savedUser);
//        LocalDateTime secondUpdateTime = firstUpdate.getUpdateTime();
//
//        // Sleep to ensure timestamp difference
//        Thread.sleep(100);
//
//        // Second update
//        firstUpdate.setUserId("test02_modified");
//        User secondUpdate = userRepository.save(firstUpdate);
//        LocalDateTime thirdUpdateTime = secondUpdate.getUpdateTime();
//
//        // Then
//        assertNotEquals(firstUpdateTime, secondUpdateTime,
//                "First and second update times should be different");
//        assertNotEquals(secondUpdateTime, thirdUpdateTime,
//                "Second and third update times should be different");
//
//        assertTrue(secondUpdateTime.isAfter(firstUpdateTime),
//                "Each update time should be chronologDataically later");
//        assertTrue(thirdUpdateTime.isAfter(secondUpdateTime),
//                "Each update time should be chronologDataically later");
//
//        logData.info("Initial update time: {}", firstUpdateTime);
//        logData.info("After first update: {}", secondUpdateTime);
//        logData.info("After second update: {}", thirdUpdateTime);
//    }
}