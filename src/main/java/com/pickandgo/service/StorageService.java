package com.pickandgo.service;

import com.pickandgo.domain.StorageRequest;
import com.pickandgo.repository.StorageRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<StorageRequest> findAll() {
        return storageRequestRepository.findAll();
    }
}
