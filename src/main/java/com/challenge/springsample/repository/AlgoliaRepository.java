package com.challenge.springsample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.springsample.model.Algolia;

@Repository
public interface AlgoliaRepository extends JpaRepository<Algolia, Long> {
    Algolia findByExternalId(String externalId);
}
