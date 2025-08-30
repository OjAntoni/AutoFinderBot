package com.example.autofinderbot.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class PagedResponse<T> extends PageImpl<T> {
    @JsonCreator
    public PagedResponse(
        @JsonProperty("content") List<T> content,
        @JsonProperty("number") int number,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long totalElements,
        @JsonProperty("pageable") JsonNode pageable,
        @JsonProperty("sort") JsonNode sort) {
        super(content, PageRequest.of(number, size), totalElements);
    }
}

