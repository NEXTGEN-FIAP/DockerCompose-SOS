package com.example.SouthernOceanSentinel_API.repository;

import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRecordRepository extends JpaRepository<PhotoRecord, Long> {
}
