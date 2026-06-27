package com.pickandgo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    private String itemName;      // 보관 물건 이름
    private String imageUrl;      // 보관 물건 이미지
    private int weight;           // 무게 (kg)
    private LocalDate startDate;  // 보관 시작일
    private LocalDate endDate;    // 보관 종료일
    private int storageCost;      // 보관비용
    private int transportCost;    // 운수비용
    private int totalCost;        // 총합

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

    public StorageRequest(String name, String address, String email, String bank,
                          String itemName, String imageUrl, int weight,
                          LocalDate startDate, LocalDate endDate,
                          int storageCost, int transportCost, int totalCost) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.bank = bank;
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.weight = weight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storageCost = storageCost;
        this.transportCost = transportCost;
        this.totalCost = totalCost;
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
    public String getItemName() { return itemName; }
    public String getImageUrl() { return imageUrl; }
    public int getWeight() { return weight; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getStorageCost() { return storageCost; }
    public int getTransportCost() { return transportCost; }
    public int getTotalCost() { return totalCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
}
