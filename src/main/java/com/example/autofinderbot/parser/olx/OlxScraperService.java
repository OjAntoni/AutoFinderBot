package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.Car;
import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.parser.service.ScraperService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.autofinderbot.common.util.APIConstants.OLX_SEARCH_URL;
import static lombok.AccessLevel.PRIVATE;

@Service
@Qualifier("olxScraper")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OlxScraperService implements ScraperService<Car> {
    DocumentService<String> documentService;
    PrerenderedStateParser prerenderedStateParser;
    JsonToOlxCarConverter jsonToOlxCarConverter;
    OlxCarToCarConverter olxCarToCarConverter;

    @Override
    public List<Car> scrape(String url) throws IOException {
        String pageHtml = documentService.load(url, rawHtml -> rawHtml.contains("__PRERENDERED_STATE__"));
        JsonNode json = prerenderedStateParser.parse(pageHtml);
        List<OlxCar> olxCars = jsonToOlxCarConverter.convert(json);
        return olxCars.stream().map(olxCarToCarConverter::convert).collect(Collectors.toList());
    }

    @Override
    public String getSearchUrl(int page) {
        return OLX_SEARCH_URL(page);
    }
}

