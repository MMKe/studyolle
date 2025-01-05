package com.studyolle.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@EqualsAndHashCode(of = "id")
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
}
