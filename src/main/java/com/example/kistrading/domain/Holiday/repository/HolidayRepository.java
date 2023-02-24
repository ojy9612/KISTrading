package com.example.kistrading.domain.Holiday.repository;

import com.example.kistrading.domain.Holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    Optional<Holiday> findByDate(LocalDateTime date);
}
