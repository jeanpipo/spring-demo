package com.challenge.springsample.service;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.challenge.springsample.model.Algolia;
import com.challenge.springsample.repository.AlgoliaRepository;
import com.challenge.springsample.repository.AlgoliaRepositoryImpl;
import com.challenge.springsample.repository.TagRepository;
import com.challenge.springsample.service.AlgoliaService;
import com.challenge.springsample.shared.Filter;
import com.challenge.springsample.shared.Pagination;
import com.challenge.springsample.shared.Sorter;
import com.challenge.springsample.util.TestContainersInitializer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = TestContainersInitializer.class)
class AlgoliaServiceTest {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private AlgoliaRepository algoliaRepository;
    @Autowired
    private AlgoliaRepositoryImpl algoliaRepositoryImpl;
    
    private AlgoliaService algoliaService;

    private List<Algolia> testAlgolias;

    @BeforeEach
    void setUp() {
        algoliaService = new AlgoliaService(algoliaRepository, algoliaRepositoryImpl, tagRepository);
        List<Algolia> algolias = generateAlgolias(6);
        algoliaRepository.saveAll(algolias);
    }

    @Test
    void testGetAlgolia() {
        Filter filter = new Filter();
        Sorter sorter = new Sorter();
        Pagination pagination = new Pagination();
        List<Algolia> expected = generateAlgolias(5);
        List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
        assertionChecker(expected, result);
    }

    @Test
    void testGetAlgoliaPaginated() {
        Filter filter = new Filter();
        Sorter sorter = new Sorter();
        { // Test with default pagination
            Pagination pagination =  new Pagination();
            List<Algolia> expected = generateAlgolias(5);
            List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
            assertionChecker(expected, result);
        }

        { // Test with offset pagination
            Pagination pagination =  new Pagination(3,3);
            
            List<Algolia> algolias = generateAlgolias(6);
            List<Algolia> expected = List.of(algolias.get(3), algolias.get(4), algolias.get(5));
            List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
            assertionChecker(expected, result);
        }

        { // Test with pagination out of range

            Pagination pagination =  new Pagination(3,103);
            
            List<Algolia> expected = List.of();
            List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
            assertionChecker(expected, result);
        }
    }

    @Test
    void testGetAlgoliaByColumn() {
        Filter filter = new Filter();
        Sorter sorter = new Sorter();
        filter.setTitle("Title 4");
        Pagination pagination =  new Pagination(2, 0);
        List<Algolia> expected = List.of(generateAlgolias(4).get(3));
        List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
        assertionChecker(expected, result);
    }

    @Test
    void testGetAlgoliaSorting() {
        Filter filter = new Filter();
        Sorter sorter = new Sorter();
        sorter.setSortField("id");
        sorter.setSortOrder("desc");
        Pagination pagination =  new Pagination(2, 0);
        List<Algolia> algolias = generateAlgolias(6);
        List<Algolia> expected = List.of(algolias.get(5), algolias.get(4));
        List<Algolia> result = algoliaService.getAlgolia(filter, sorter, pagination);
        assertionChecker(expected, result);
    }

    private void assertionChecker(List<Algolia> expected, List<Algolia> result) {
        Assertions.assertThat(expected)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyElementsOf(result);
    }

    private List<Algolia> generateAlgolias(int max) {
        testAlgolias = new java.util.ArrayList<>();
        for (int i = 1; i <= max; i++) {
            Algolia algolia = new Algolia();
            algolia.setExternalId("ext-" + i);
            algolia.setTitle("Title " + i);
            algolia.setUrl("http://example.com/" + i);
            algolia.setAuthorName("Author " + i);
            algolia.setComment("Comment text " + i);
            algolia.setCreatedTime("2023-01-0" + i + "T12:00:00Z");
            
            testAlgolias.add(algolia);
        }

        return testAlgolias;
    }
}