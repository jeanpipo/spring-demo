package com.challenge.springsample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.springsample.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByTagNameIn(List<String> tagName);
}
