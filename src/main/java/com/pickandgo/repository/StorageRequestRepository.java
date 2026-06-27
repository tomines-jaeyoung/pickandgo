package com.pickandgo.repository;

import com.pickandgo.domain.StorageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRequestRepository extends JpaRepository<StorageRequest, Long> {
}
