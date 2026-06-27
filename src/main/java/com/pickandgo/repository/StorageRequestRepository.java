package com.pickandgo.repository;

import com.pickandgo.domain.StorageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StorageRequestRepository extends JpaRepository<StorageRequest, Long> {
    List<StorageRequest> findByEmail(String email);
}
