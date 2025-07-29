package com.challenge.springsample.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.springsample.model.Algolia;
import com.challenge.springsample.service.AlgoliaService;
import com.challenge.springsample.shared.Filter;
import com.challenge.springsample.shared.Pagination;
import com.challenge.springsample.shared.Sorter;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping(path = "api/algolia")
public class AlgoliaController {

    AlgoliaService algoliaService;

    public AlgoliaController(AlgoliaService algoliaService) {
        this.algoliaService = algoliaService;
    }

    @GetMapping
    public ResponseEntity<List<Algolia>> algoliaDataGet(@Nullable Sorter sorter,
            @Nullable Filter filter,
            @Nullable Pagination pagination) {
        try {
            return ResponseEntity.ok(algoliaService.getAlgolia(filter, sorter, pagination));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(path = "algolia/{id}")
    public ResponseEntity<String> algoliaDataDeleteRecord(@PathVariable("id") Long id) {
        try {
            algoliaService.algoliaDataDeleteRecord(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching data: " + e.getMessage());
        }
    }

    @GetMapping(path = "initialize")
    public ResponseEntity<Void> initializeDB() {
        boolean isDBInitilized = algoliaService.callAlgoliaApi();
        if (isDBInitilized) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
