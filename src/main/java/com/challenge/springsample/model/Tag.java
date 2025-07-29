package com.challenge.springsample.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("tag")
    @Column(unique = true)
    private String tagName;

    @ManyToMany(mappedBy = "tag", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    private List<Algolia> algolia = new ArrayList();

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
