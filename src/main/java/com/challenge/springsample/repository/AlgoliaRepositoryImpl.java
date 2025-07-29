package com.challenge.springsample.repository;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.challenge.springsample.model.Algolia;
import com.challenge.springsample.model.Tag;
import com.challenge.springsample.shared.Filter;
import com.challenge.springsample.shared.Pagination;
import com.challenge.springsample.shared.Sorter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class AlgoliaRepositoryImpl implements AlgoliaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method generates a dynamic query based on the provided filter.
     * this query is exclusive since I cannot validate any requirement. 
     * in case you want a inclusive query, then I can change it.
     */
    @Override
    public List<Algolia> findByColumns(Filter filter, Sorter sorter, Pagination pagination) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Algolia> query = cb.createQuery(Algolia.class);
        Root<Algolia> root = query.from(Algolia.class);

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getAuthorName() != null) {
            predicates.add(cb.equal(root.get("authorName"), filter.getAuthorName()));
        }

        if (filter.getTitle() != null) {
            predicates.add(cb.equal(root.get("title"), filter.getTitle()));
        }

        // I know this is not the best way to handle date/timestamp, but in order to support multiple filters at the same time
        // including months I had to use a string representation of the month.
        if (filter.getMonth() != null) {
            try {
                Month month = Month.valueOf(filter.getMonth().toUpperCase(Locale.ENGLISH));
                String monthFormat = String.format("%02d", month.getValue());
                predicates.add(cb.like(root.get("createdTime"),  "%-" + monthFormat + "-%"));
            } catch (IllegalArgumentException e) {
                // Handle invalid month value 
                throw new IllegalArgumentException("Invalid month value: " + filter.getMonth());
            }
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            Join<Algolia, Tag> tagJoin = root.join("tag"); 
            predicates.add(tagJoin.get("tagName").in(filter.getTags()));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        if (sorter != null && sorter.getSortField() != null) {
            if (sorter.getSortOrder() != null && sorter.getSortOrder().equalsIgnoreCase("asc")) {
                query.orderBy(cb.asc(root.get(sorter.getSortField())));
            } else {
                query.orderBy(cb.desc(root.get(sorter.getSortField())));
            }
        }
        return entityManager.createQuery(query)
        .setMaxResults(pagination.getSize())
        .setFirstResult(pagination.getOffset())
        .getResultList();
    }
}
