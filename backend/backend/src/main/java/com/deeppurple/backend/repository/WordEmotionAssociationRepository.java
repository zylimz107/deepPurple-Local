package com.deeppurple.backend.repository;

import com.deeppurple.backend.entity.WordEmotionAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordEmotionAssociationRepository extends JpaRepository<WordEmotionAssociation, Long> {}
