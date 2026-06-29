package com.pickandgo.controller;

import com.pickandgo.domain.Category;
import com.pickandgo.domain.Member;
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

        model.addAttribute("location", location);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("maxPrice", maxPrice);

        return "products";
    }

    /** 상품 상세 */
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
                                   HttpSession session,
                                   Model model) {
        if (category == null) category = Category.ETC;
        Member loginMember = (Member) session.getAttribute("loginMember");
        String sellerEmail = (loginMember != null) ? loginMember.getEmail() : null;

        Product saved = productService.registerForCollection(name, price, description, category, location, image, sellerEmail, uploadDir);
        model.addAttribute("product", saved);
        return "organizeMatch";
    }

    @PostMapping("/organize")
    public String organizeSubmit(@RequestParam String name,
                                  @RequestParam int price,
                                  @RequestParam(required = false) String description,
                                  @RequestParam Category category,
                                  @RequestParam String location,
                                  @RequestParam(required = false) MultipartFile image,
                                  HttpSession session,
                                  Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        String sellerEmail = (loginMember != null) ? loginMember.getEmail() : null;

        Product saved = productService.register(name, price, description, category, location, image, sellerEmail, uploadDir);
        model.addAttribute("product", saved);
        return "redirect:/products/" + saved.getId();
    }

    /** 판매 완료 처리 */
    @PostMapping("/products/{id}/complete")
    public String completeProductSale(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        productService.completeSale(id);
        return "redirect:/mypage";
    }

    @GetMapping("/products/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        Product product = productService.findById(id);
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (!product.getSellerEmail().equals(loginMember.getEmail())) {
            throw new IllegalStateException("본인의 상품만 수정할 수 있습니다.");
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", Category.values());
        return "productEdit";
    }

    @PostMapping("/products/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam int price,
                             @RequestParam(required = false) String description,
                             @RequestParam Category category,
                             @RequestParam String location,
                             @RequestParam(required = false) MultipartFile image,
                             HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        productService.update(id, name, price, description, category, location, image, uploadDir);
        return "redirect:/mypage";
    }

    @PostMapping("/products/{id}/relist")
    public String relist(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        productService.relist(id);
        return "redirect:/mypage";
    }

    @PostMapping("/products/{id}/recollect")
    public String recollect(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        productService.recollect(id);
        return "redirect:/mypage";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        productService.delete(id);
        return "redirect:/mypage";
    }
}
