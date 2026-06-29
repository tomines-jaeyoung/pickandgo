package com.pickandgo.controller;

import com.pickandgo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 랜딩 페이지 컨트롤러.
 * React의 1학기 HTML 랜딩페이지(가구 수거/보관/판매 애니메이션 소개)를
 * Thymeleaf 정적 화면 + CSS 애니메이션으로 재구현.
 */
@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page, Model model) {
        org.springframework.data.domain.Page<com.pickandgo.domain.Product> paged = productService.findLatestPaged(page, 10);
        model.addAttribute("latestProducts", paged.getContent());
        model.addAttribute("page", paged);
        return "index";
    }
}
