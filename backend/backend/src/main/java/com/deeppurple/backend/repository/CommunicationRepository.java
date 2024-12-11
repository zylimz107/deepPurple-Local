package com.deeppurple.backend.repository;

import com.deeppurple.backend.entity.Communication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunicationRepository extends JpaRepository<Communication, Long> {
    List<Communication> findByPrimaryEmotion(String primaryEmotion);

    List<Communication> findByTimestampAfter(LocalDateTime timestamp);

    Optional<Communication> findById(Long id);

    // Example of pagination and sorting
    Page<Communication> findByPrimaryEmotion(String primaryEmotion, Pageable pageable);

    // New queries for extended functionality
    List<Communication> findByModelName(String modelName); // Fetch by model name

}
