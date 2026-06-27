package com.pickandgo.controller;

import com.pickandgo.service.InquiryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 문의하기 페이지 - 1학기/React에는 없던, 이번에 새로 추가한 페이지 */
@Controller
public class ContactController {

    private final InquiryService inquiryService;

    public ContactController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping("/contact")
    public String form() {
        return "contact";
    }

    @PostMapping("/contact")
    public String submit(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String title,
                          @RequestParam String content,
                          Model model) {
        model.addAttribute("inquiry", inquiryService.submit(name, email, title, content));
        return "contactComplete";
    }
}
