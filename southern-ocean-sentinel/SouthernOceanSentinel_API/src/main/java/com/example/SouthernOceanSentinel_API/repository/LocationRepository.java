package com.example.SouthernOceanSentinel_API.repository;

import com.example.SouthernOceanSentinel_API.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
