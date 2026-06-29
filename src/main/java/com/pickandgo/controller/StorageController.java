package com.pickandgo.controller;

import com.pickandgo.domain.StorageRequest;
import com.pickandgo.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

/**
 * 맡겨두기(보관 서비스) 신청 컨트롤러.
 * React StorePage.jsx (이름/주소/이메일/결제방법 입력 후 alert 안내) 대응.
 */
@Controller
@org.springframework.web.bind.annotation.RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public String form(HttpSession session, Model model) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        model.addAttribute("member", session.getAttribute("loginMember"));
        return "storage";
    }

    @PostMapping
    public String submit(@RequestParam String name,
                          @RequestParam String address,
                          @RequestParam String email,
                          @RequestParam String bank,
                          @RequestParam String itemName,
                          @RequestParam(required = false) MultipartFile image,
                          @RequestParam int weight,
                          @RequestParam String startDate,
                          @RequestParam String endDate,
                          @RequestParam int storageCost,
                          @RequestParam int transportCost,
                          Model model) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        StorageRequest saved = storageService.request(name, address, email, bank, itemName, image, weight, start, end, storageCost, transportCost, uploadDir);
        model.addAttribute("request", saved);
        return "storageComplete";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        com.pickandgo.domain.Member loginMember = (com.pickandgo.domain.Member) session.getAttribute("loginMember");
        StorageRequest req = storageService.findById(id);
        if (!req.getEmail().equals(loginMember.getEmail())) {
            throw new IllegalStateException("본인의 보관 신청만 수정할 수 있습니다.");
        }
        model.addAttribute("storageRequest", req);
        model.addAttribute("member", loginMember);
        return "storageEdit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String address,
                             @RequestParam String email,
                             @RequestParam String bank,
                             @RequestParam String itemName,
                             @RequestParam(required = false) MultipartFile image,
                             @RequestParam int weight,
                             @RequestParam String startDate,
                             @RequestParam String endDate,
                             @RequestParam int storageCost,
                             @RequestParam int transportCost,
                             HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        storageService.update(id, name, address, email, bank, itemName, image, weight, start, end, storageCost, transportCost, uploadDir);
        return "redirect:/mypage";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginMember") == null) return "redirect:/login";
        storageService.delete(id);
        return "redirect:/mypage";
    }
}
