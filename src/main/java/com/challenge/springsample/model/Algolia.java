package com.challenge.springsample.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Algolia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("author")
    private String authorName;

    @Nullable
    @JsonSetter("comment_text")
    @Column(columnDefinition = "Text")
    private String comment;

    @JsonSetter("story_title")
    @JsonAlias("title")
    @Column(columnDefinition = "Text")
    private String title;

    @Nullable
    @JsonSetter("story_url")
    @JsonAlias("url")
    @Column(columnDefinition = "Text")
    private String url;

    @ManyToMany
    @JoinTable(name = "algolia_tag", 
        joinColumns = @JoinColumn(name = "algolia_id"), 
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tag;

    @JsonSetter("updated_at")
    private String lastUpdated;

    @JsonSetter("created_at")
    private String createdTime;

    @Column(columnDefinition = "Text", unique = true)
    @JsonSetter("objectID")
    private String externalId;

    @JsonSetter("_tags")
    public void setTag(List<String> tag) {
        this.tag = tag.stream()
                .map(Tag::new)
                .toList();
    }

    public void setNewTag(List<Tag> tag) {
        this.tag = tag;
    }

    @JsonGetter("tags")
    public List<String> getTag() {
        return tag.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }
}
