package com.example.autofinderbot.parser.service;

import java.io.IOException;
import java.util.List;

public interface ScraperService<T> {
    /**
     * Scrape the given URL and return a list of domain objects.
     */
    List<T> scrape(String url) throws IOException;

    String getSearchUrl(int page);
}
