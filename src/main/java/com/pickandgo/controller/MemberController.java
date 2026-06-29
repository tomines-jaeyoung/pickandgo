package com.pickandgo.controller;

import com.pickandgo.domain.Member;
import com.pickandgo.domain.Product;
import com.pickandgo.domain.StorageRequest;
import com.pickandgo.service.MemberService;
import com.pickandgo.service.ProductService;
import com.pickandgo.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final StorageService storageService;
    private final ProductService productService;

    public MemberController(MemberService memberService, StorageService storageService, ProductService productService) {
        this.memberService = memberService;
        this.storageService = storageService;
        this.productService = productService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                            @RequestParam String address,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String bank,
                            Model model) {
        try {
            Member member = memberService.register(name, address, email, password, bank);
            model.addAttribute("member", member);
            return "registerComplete";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        try {
            Member member = memberService.login(email, password);
            session.setAttribute("loginMember", member);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mypage")
    @SuppressWarnings("unchecked")
    public String mypage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 수거대기 상태 자동 갱신
        productService.autoUpdateStatus(loginMember.getEmail());

        // 보관소 신청 내역 조회
        List<StorageRequest> storageRequests = storageService.findByEmail(loginMember.getEmail());
        model.addAttribute("storageRequests", storageRequests);

        // 정리(수거) 및 판매(중고거래) 내역 조회
        List<Product> myProducts = productService.findBySellerEmail(loginMember.getEmail());
        List<Product> cleanupList = myProducts.stream()
                .filter(p -> "수거대기".equals(p.getStatus()) || "수거완료".equals(p.getStatus()))
                .collect(Collectors.toList());
        List<Product> marketList = myProducts.stream()
                .filter(p -> "판매중".equals(p.getStatus()) || "판매완료".equals(p.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("cleanupList", cleanupList);
        model.addAttribute("marketList", marketList);

        // 장바구니 요약 리스트 조회
        Set<Long> cartIds = (Set<Long>) session.getAttribute("cartIds");
        List<Product> cartItems = (cartIds == null) ? List.of() :
                cartIds.stream().map(productService::findById).collect(Collectors.toList());
        model.addAttribute("cartItems", cartItems);

        model.addAttribute("member", loginMember);
        return "mypage";
    }

    @PostMapping("/mypage/update")
    @SuppressWarnings("unchecked")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String address,
                                @RequestParam String email,
                                @RequestParam(required = false) String password,
                                @RequestParam String bank,
                                HttpSession session,
                                Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }
        try {
            Member updated = memberService.update(loginMember.getId(), name, address, email, password, bank);
            session.setAttribute("loginMember", updated);
            return "redirect:/mypage?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("member", loginMember);
            model.addAttribute("storageRequests", storageService.findByEmail(loginMember.getEmail()));

            List<Product> myProducts = productService.findBySellerEmail(loginMember.getEmail());
            List<Product> cleanupList = myProducts.stream()
                    .filter(p -> "수거대기".equals(p.getStatus()) || "수거완료".equals(p.getStatus()))
                    .collect(Collectors.toList());
            List<Product> marketList = myProducts.stream()
                    .filter(p -> "판매중".equals(p.getStatus()) || "판매완료".equals(p.getStatus()))
                    .collect(Collectors.toList());
            model.addAttribute("cleanupList", cleanupList);
            model.addAttribute("marketList", marketList);

            Set<Long> cartIds = (Set<Long>) session.getAttribute("cartIds");
            List<Product> cartItems = (cartIds == null) ? List.of() :
                    cartIds.stream().map(productService::findById).collect(Collectors.toList());
            model.addAttribute("cartItems", cartItems);
            return "mypage";
        }
    }
}
