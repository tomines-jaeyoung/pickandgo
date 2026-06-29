package com.pickandgo.service;

import com.pickandgo.domain.Inquiry;
import com.pickandgo.repository.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    public Inquiry submit(String name, String email, String title, String content) {
        return inquiryRepository.save(new Inquiry(name, email, title, content));
    }

    public java.util.List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }
}
