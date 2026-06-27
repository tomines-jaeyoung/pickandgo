package com.pickandgo.repository;

import com.pickandgo.domain.Category;
import com.pickandgo.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * React SellPage.jsx의 filteredProducts (useMemo) 로직을 JPQL로 옮긴 것.
     * 지역(location) -> 검색어(keyword) -> 카테고리(category) -> 가격(maxPrice) 순으로
     * AND 조건을 적용해 모든 조건을 만족하는 상품만 조회한다.
     * 파라미터가 null이면 해당 조건은 무시된다 (= React의 "전체" 버튼과 동일한 동작).
     */
    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND (:location IS NULL OR p.location LIKE CONCAT('%', :location, '%'))
              AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
              AND (:category IS NULL OR p.category = :category)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            ORDER BY p.id DESC
            """)
    Page<Product> search(@Param("location") String location,
                          @Param("keyword") String keyword,
                          @Param("category") Category category,
                          @Param("maxPrice") Integer maxPrice,
                          Pageable pageable);

    /** 랜딩페이지 Sale 슬라이더용 - 최신 등록 N개 */
    Page<Product> findByOnSaleTrueOrderByIdDesc(Pageable pageable);
}
