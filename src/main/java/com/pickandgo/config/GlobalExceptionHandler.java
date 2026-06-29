package com.pickandgo.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String STARTUP_ID = UUID.randomUUID().toString();

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("startupId", STARTUP_ID);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
