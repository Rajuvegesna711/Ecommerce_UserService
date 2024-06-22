package com.vegesna.userservice.Models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Generated;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Timestamp createdAt;
}
