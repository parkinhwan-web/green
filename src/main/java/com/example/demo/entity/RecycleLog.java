package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class RecycleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long analysisId;
    private String disposalCategory;
    private String disposalMethod;

    private ZonedDateTime createdAt = ZonedDateTime.now();
}
