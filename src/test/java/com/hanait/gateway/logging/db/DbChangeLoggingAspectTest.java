package com.hanait.gateway.logging.db;

import com.hanait.gateway.model.UserTest;
import com.hanait.gateway.repository.UserRepositoryTest;
import jakarta.transaction.Transactional;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DbChangeLoggingAspect.class)
class DbChangeLoggingAspectTest {

    @Autowired
    private UserRepositoryTest userRepositoryTest;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("db_change");

        UserTest user = new UserTest(1L, "Jane Doe", "jane@example.com");
        userRepositoryTest.save(user);
    }

    @Test
    @Transactional
    void testUpdateUser_logsDbChange() {
        // Given
        UserTest user = userRepositoryTest.findById(1L).orElseThrow();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Whenee2  2
        userRepositoryTest.save(user); // Aspect에서 이 시점 전후로 DB 로그가 찍힘

        // Then
        List<Document> logs = mongoTemplate.findAll(Document.class, "db_change");
        assertThat(logs).hasSize(1);

        Document log = logs.get(0);
        assertThat(log.getString("db_table")).isEqualTo("user");
        assertThat(log.get("operation")).isEqualTo("UPDATE");

        Map<String, Object> changed = (Map<String, Object>) log.get("changed_data");
        Map<String, Object> previous = (Map<String, Object>) log.get("previous_data");

        assertThat(changed.get("name")).isEqualTo("John Doe");
        assertThat(previous.get("name")).isEqualTo("Jane Doe");
    }
}
