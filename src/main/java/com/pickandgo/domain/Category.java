package com.pickandgo.domain;

/**
 * 상품 카테고리
 * React 프로젝트의 카테고리 필터(침대/책상/수납장/의자/책상/테이블/소파/스타일러/TV/장롱/식탁/세탁기) 대응
 */
public enum Category {
    BED("침대"),
    DESK("책상"),
    STORAGE("수납장"),
    CHAIR("의자"),
    TABLE("테이블"),
    SOFA("소파"),
    STYLER("스타일러"),
    TV("TV"),
    WARDROBE("장롱"),
    DINING_TABLE("식탁"),
    WASHER("세탁기"),
    ETC("기타");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
