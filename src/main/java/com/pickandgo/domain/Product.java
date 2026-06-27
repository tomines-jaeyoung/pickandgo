package com.pickandgo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 중고 가구 상품 엔티티.
 * React 프로젝트의 saleItems / transactionItems / basicProducts 데이터를
 * 하나의 DB 테이블로 통합한 형태.
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    /** 지역 (예: 서울특별시 용산구 보광동) */
    private String location;

    /** 업로드된 이미지 경로 (/uploads/xxx.jpg) */
    private String imageUrl;

    /** 별점 (0~5) */
    private double rating;

    /** true: 판매중, false: 판매완료 */
    private boolean onSale = true;

    private LocalDateTime createdAt;

    protected Product() {
        // JPA 기본 생성자
    }

    public Product(String name, int price, String description, Category category,
                    String location, String imageUrl, double rating) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.location = location;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.onSale = true;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isOnSale() { return onSale; }
    public void setOnSale(boolean onSale) { this.onSale = onSale; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    /** 천 단위 콤마가 포함된 가격 문자열 (예: 480,000) - React의 toLocaleString() 대응 */
    public String getFormattedPrice() {
        return String.format("%,d", price);
    }
}
