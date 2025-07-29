package com.challenge.springsample.repository;

import java.util.List;

import com.challenge.springsample.model.Algolia;
import com.challenge.springsample.shared.Filter;
import com.challenge.springsample.shared.Pagination;
import com.challenge.springsample.shared.Sorter;

public interface AlgoliaRepositoryCustom {
    List<Algolia> findByColumns(Filter filter, Sorter sorter, Pagination pagination);
}
