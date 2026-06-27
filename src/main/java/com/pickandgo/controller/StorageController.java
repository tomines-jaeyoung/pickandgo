package com.pickandgo.controller;

import com.pickandgo.domain.StorageRequest;
import com.pickandgo.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 맡겨두기(보관 서비스) 신청 컨트롤러.
 * React StorePage.jsx (이름/주소/이메일/결제방법 입력 후 alert 안내) 대응.
 */
@Controller
@org.springframework.web.bind.annotation.RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public String form() {
        return "storage";
    }

    @PostMapping
    public String submit(@RequestParam String name,
                          @RequestParam String address,
                          @RequestParam String email,
                          @RequestParam String bank,
                          Model model) {
        StorageRequest saved = storageService.request(name, address, email, bank);
        model.addAttribute("request", saved);
        return "storageComplete";
    }
}
