package com.pickandgo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 문의하기(Contact) 엔티티 - 추가로 구현한 페이지
 */
@Entity
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    protected Inquiry() {
    }

    public Inquiry(String name, String email, String title, String content) {
        this.name = name;
        this.email = email;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
