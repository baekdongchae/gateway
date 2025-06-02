package com.hanait.gateway.logging.db;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "db_change")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MongoDbChangeLog {

    @Id
    private String id;

    private String uuid;
    private Instant timestamp;
    private String requestUserCode;
    private String dbTable;
    private String operation;
    private Map<String, Object> changedData;
    private Map<String, Object> previousData;
}
