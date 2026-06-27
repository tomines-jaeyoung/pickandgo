package com.pickandgo.controller;

import com.pickandgo.domain.Product;
import com.pickandgo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 장바구니 - React 상세모달의 "장바구니에 추가" 버튼 동작을
 * 실제 페이지+세션으로 구현한 추가 기능.
 */
@Controller
public class CartController {

    private static final String CART_SESSION_KEY = "cartIds";

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/cart/{id}")
    @SuppressWarnings("unchecked")
    public String add(@PathVariable Long id, HttpSession session) {
        Set<Long> cart = (Set<Long>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new LinkedHashSet<>();
        }
        cart.add(id);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    @SuppressWarnings("unchecked")
    public String view(HttpSession session, Model model) {
        Set<Long> cart = (Set<Long>) session.getAttribute(CART_SESSION_KEY);
        List<Product> items = (cart == null) ? List.of() :
                cart.stream().map(productService::findById).collect(Collectors.toList());

        int total = items.stream().mapToInt(Product::getPrice).sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/remove/{id}")
    @SuppressWarnings("unchecked")
    public String remove(@PathVariable Long id, HttpSession session) {
        Set<Long> cart = (Set<Long>) session.getAttribute(CART_SESSION_KEY);
        if (cart != null) {
            cart.remove(id);
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    @SuppressWarnings("unchecked")
    public String checkout(@RequestParam("itemIds") List<Long> itemIds, HttpSession session, Model model) {
        Set<Long> cart = (Set<Long>) session.getAttribute(CART_SESSION_KEY);
        if (cart != null) {
            cart.removeAll(itemIds);
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        List<Product> purchasedItems = itemIds.stream()
                .map(productService::findById)
                .collect(Collectors.toList());
        int total = purchasedItems.stream().mapToInt(Product::getPrice).sum();

        model.addAttribute("items", purchasedItems);
        model.addAttribute("total", total);
        return "cartComplete";
    }
}
