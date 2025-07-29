package com.challenge.springsample.shared;

import java.util.List;

import lombok.Data;

@Data
public class Filter {
    private String authorName;
    private List<String> tags;
    private String title;
    private String month;
}
