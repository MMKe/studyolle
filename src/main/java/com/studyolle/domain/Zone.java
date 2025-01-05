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
public class Zone {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 영문 도시 이름
     */
    @Column(nullable = false)
    private String city;

    /**
     * 한국어 도시 이름
     */
    @Column(nullable = false)
    private String localNameOfCity;

    /**
     * 주 이름
     */
    private String province;
}
