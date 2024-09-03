package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EqualsAndHashCode(of = "id") // 연관관계가 복잡해질때, 무한루프가 발생할 수 있으므로 id만 사용하여 동일성/동등성 비교하도록 한다.
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @Getter
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Getter
    private String password;

    private boolean emailVerified;

    @Getter
    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location; // varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrolmentResultByEmail;

    private boolean studyEnrolmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    public Account() {
    }

    public Account(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.studyCreatedByWeb = true;
        this.studyEnrolmentResultByWeb = true;
        this.studyUpdatedByWeb = true;
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}
