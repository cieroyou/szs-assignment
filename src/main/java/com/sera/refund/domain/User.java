package com.sera.refund.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password; // 🔒 암호화 필요

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String regNo; // 주민등록번호 (암호화 필요)


    private User(String userId, String password, String name, String regNo) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
    }

    public static User of(String userId, String password, String name, String regNo) {
        return new User(userId, password, name, regNo);
    }
}
