package com.pickandgo.controller;

import com.pickandgo.domain.Category;
import com.pickandgo.domain.Product;
import com.pickandgo.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 중고판매(Products) 페이지 + 물건 업로드(Organize) 페이지 컨트롤러.
 *
 * React SellPage.jsx의 다중조건 필터링(지역->검색어->카테고리->가격)을
 * ProductService.search() 호출 한 줄로 대체했고,
 * React useMemo의 "리렌더링 시 재연산 방지" 의도는
 * 여기서는 DB 쿼리 자체가 필요한 데이터만 가져오는 것으로 대응한다.
 */
@Controller
@RequestMapping
public class ProductController {

    private final ProductService productService;

    @Value("${pickandgo.upload.dir}")
    private String uploadDir;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 판매하기(중고판매) 페이지 - GET /products?location=&keyword=&category=&maxPrice=&page= */
    @GetMapping("/products")
    public String list(@RequestParam(required = false) String location,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Category category,
                        @RequestParam(required = false) Integer maxPrice,
                        @RequestParam(defaultValue = "1") int page,
                        HttpSession session,
                        Model model) {

        if (session.getAttribute("loginMember") == null) return "redirect:/login";

        Page<Product> result = productService.search(location, keyword, category, maxPrice, page);

        model.addAttribute("products", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("categories", Category.values());

        // 검색폼 값 유지 (React의 controlled input value 대응)
        model.addAttribute("location", location);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("maxPrice", maxPrice);

        return "products";
    }

    /** 상품 상세 (React의 ProductDetailModal -> 별도 페이지로 구현) */
    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "productDetail";
    }

    /** 정리하기(Organize) - 업로드 폼 페이지 */
    @GetMapping("/organize")
    public String organizeForm(HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        return "organize";
    }

    /** 정리하기 - 수거 요청 (등록 후 매칭 페이지로 이동) */
    @PostMapping("/organize/collect")
    public String organizeCollect(@RequestParam String name,
                                   @RequestParam(defaultValue = "0") int price,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) Category category,
                                   @RequestParam(required = false) String location,
                                   @RequestParam(required = false) MultipartFile image,
                                   Model model) {
        if (category == null) category = Category.ETC;
        Product saved = productService.registerForCollection(name, price, description, category, location, image, uploadDir);
        model.addAttribute("product", saved);
        return "organizeMatch";
    }


    public String organizeSubmit(@RequestParam String name,
                                  @RequestParam int price,
                                  @RequestParam(required = false) String description,
                                  @RequestParam Category category,
                                  @RequestParam String location,
                                  @RequestParam(required = false) MultipartFile image,
                                  Model model) {

        Product saved = productService.register(name, price, description, category, location, image, uploadDir);
        model.addAttribute("product", saved);
        return "redirect:/products/" + saved.getId();
    }
}
