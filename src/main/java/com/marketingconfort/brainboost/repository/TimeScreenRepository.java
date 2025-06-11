package com.marketingconfort.brainboost.repository;

import com.marketingconfort.brainboost_common.usermanagement.models.TimeScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeScreenRepository extends JpaRepository<TimeScreen, Long> {
}
