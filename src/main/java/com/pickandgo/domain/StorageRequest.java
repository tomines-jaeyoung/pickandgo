package com.pickandgo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 맡겨두기(보관) 서비스 신청 엔티티
 * React StorePage.jsx (이름/주소/이메일/결제방법) 대응
 */
@Entity
@Table(name = "storage_request")
public class StorageRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String email;

    private String bank;

    /** 처리 상태: 접수, 보관중, 반환완료 */
    private String status = "접수";

    private LocalDateTime requestedAt;

    protected StorageRequest() {
    }

    public StorageRequest(String name, String address, String email, String bank) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.bank = bank;
        this.status = "접수";
        this.requestedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getBank() { return bank; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
}
