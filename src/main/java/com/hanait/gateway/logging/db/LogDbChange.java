package com.hanait.gateway.logging.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDbChange {
    String table();       // ex) "users"
    String operation();   // ex) "UPDATE", "INSERT", "DELETE"
}