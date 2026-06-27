package com.pickandgo.service;

import com.pickandgo.domain.StorageRequest;
import com.pickandgo.repository.StorageRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StorageService {

    private final StorageRequestRepository storageRequestRepository;

    public StorageService(StorageRequestRepository storageRequestRepository) {
        this.storageRequestRepository = storageRequestRepository;
    }

    @Transactional
    public StorageRequest request(String name, String address, String email, String bank) {
        return storageRequestRepository.save(new StorageRequest(name, address, email, bank));
    }

    @Transactional
    public StorageRequest request(String name, String address, String email, String bank,
                                  String itemName, MultipartFile imageFile, int weight,
                                  LocalDate startDate, LocalDate endDate,
                                  int storageCost, int transportCost, String uploadDir) {
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new IllegalArgumentException("보관 종료일은 시작일보다 이후여야 합니다.");
        }

        String imageUrl = saveImage(imageFile, uploadDir);
        int totalCost = storageCost + transportCost;

        StorageRequest request = new StorageRequest(
                name, address, email, bank,
                itemName, imageUrl, weight,
                startDate, endDate,
                storageCost, transportCost, totalCost
        );
        return storageRequestRepository.save(request);
    }

    public List<StorageRequest> findByEmail(String email) {
        return storageRequestRepository.findByEmail(email);
    }

    private String saveImage(MultipartFile file, String uploadDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);

            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String savedName = UUID.randomUUID() + ext;

            Path target = dirPath.resolve(savedName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + savedName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    public List<StorageRequest> findAll() {
        return storageRequestRepository.findAll();
    }
}
