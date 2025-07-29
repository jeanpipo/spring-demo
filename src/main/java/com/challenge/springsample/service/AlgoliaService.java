package com.challenge.springsample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.challenge.springsample.model.Algolia;
import com.challenge.springsample.model.Tag;
import com.challenge.springsample.repository.AlgoliaRepository;
import com.challenge.springsample.repository.AlgoliaRepositoryImpl;
import com.challenge.springsample.repository.TagRepository;
import com.challenge.springsample.shared.Filter;
import com.challenge.springsample.shared.Pagination;
import com.challenge.springsample.shared.Result;
import com.challenge.springsample.shared.Sorter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableScheduling
public class AlgoliaService {
    private final AlgoliaRepository algoliaRepository;
    private final AlgoliaRepositoryImpl algoliaRepositoryImpl;
    private final TagRepository tagRepository;

    public AlgoliaService(AlgoliaRepository algoliaRepository,
            AlgoliaRepositoryImpl algoliaRepositoryImpl,
            TagRepository tagRepository) {
        this.algoliaRepository = algoliaRepository;
        this.algoliaRepositoryImpl = algoliaRepositoryImpl;
        this.tagRepository = tagRepository;
    }

    public List<Algolia> getAlgolia(Filter filter, Sorter sorter, Pagination pagination) {
        return algoliaRepositoryImpl.findByColumns(filter, sorter, pagination);
    }

    public void algoliaDataDeleteRecord(Long id) {
        algoliaRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 * * * *")
    public boolean callAlgoliaApi() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://hn.algolia.com")
                .build();
        Result result = restClient.get()
                .uri("/api/v1/search_by_date?query=java")
                .retrieve()
                .body(Result.class);

        AtomicInteger size = new AtomicInteger(result.getHits().size());
        result.getHits()
                .stream()
                .map(object -> new ObjectMapper().convertValue(object, Algolia.class))
                .forEach(algolia -> {
                    try {
                        List<Tag> existingTags = tagRepository.findByTagNameIn(algolia.getTag());
                        List<Tag> tagsToSave = new ArrayList();
                        if (existingTags != null
                                && algolia.getTag() != null
                                && existingTags.size() != algolia.getTag().size()) {

                            algolia.getTag().forEach(tag -> {
                                Optional<Tag> maybeTag = existingTags.stream()
                                        .filter(t -> t.getTagName().equalsIgnoreCase(tag))
                                        .findAny();
                                if (maybeTag.isPresent()) {
                                    tagsToSave.add(maybeTag.get());
                                } else {
                                    tagsToSave.add(tagRepository.save(new Tag(tag)));
                                }
                            });

                            algolia.setNewTag(tagsToSave);
                            algoliaRepository.save(algolia);
                        } else {
                            algolia.setNewTag(existingTags);
                            algoliaRepository.save(algolia);
                        }
                    } catch (DataIntegrityViolationException cve) {
                        // If the record already exists, we update it
                        Algolia auxAlgolia = algoliaRepository.findByExternalId(algolia.getExternalId());
                        if (auxAlgolia != null) {
                            algolia.setId(auxAlgolia.getId());
                        }
                        algoliaRepository.save(algolia);
                    }
                    size.decrementAndGet();
                });
        // If the size is 0, it means that all records were processed successfully
        return size.get() == 0;
    }
}
