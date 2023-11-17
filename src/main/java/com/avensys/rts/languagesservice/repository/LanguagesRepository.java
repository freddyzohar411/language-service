package com.avensys.rts.languagesservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.languagesservice.entity.LanguagesEntity;

import java.util.List;

public interface LanguagesRepository extends JpaRepository<LanguagesEntity, Integer> {
    List<LanguagesEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);
}
