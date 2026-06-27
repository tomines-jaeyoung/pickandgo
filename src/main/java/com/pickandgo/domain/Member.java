package com.pickandgo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 회원 엔티티 (React 프로젝트의 회원가입 / 맡겨두기 신청 폼 통합)
 */
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    /** 결제(환불)은행 - 하나은행 / 국민은행 / 신한은행 / 카카오뱅크 */
    private String bank;

    private LocalDateTime joinedAt;

    protected Member() {
    }

    public Member(String name, String address, String email, String password, String bank) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.password = password;
        this.bank = bank;
        this.joinedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
}
