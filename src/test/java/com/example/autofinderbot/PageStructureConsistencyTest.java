package com.example.autofinderbot;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.service.DocumentService;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;

import static com.example.autofinderbot.shared.APIConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class PageStructureConsistencyTest extends BaseSpringBootTest {
    @Autowired
    DocumentService documentService;
    Document document;

    @BeforeEach
    void beforeEach() throws IOException {
        document = documentService.load(SEARCH_URL);
    }

    @Test
    void pagesSelectorIsValid_PostTC() {
        assertThat(document.select(PAGES_SELECTOR)).isNotEmpty();
    }

    @Test
    void carUrlSelectorIsValid_PostTC() {
        assertThat(document.select(CAR_URL_SELECTOR)).isNotEmpty();
    }

    @Test
    void carStatisticsSelectorsAreValid_PostTC() throws Exception {
        assumeFalse(document.select(CAR_URL_SELECTOR).isEmpty());

        String url = Objects.requireNonNull(document.select(CAR_URL_SELECTOR).first()).attribute("href").getValue();
        Document carPage = documentService.load(url);

        assertThat(carPage.select(CAR_TITLE_SELECTOR)).isNotEmpty();
        assertThat(carPage.select(CAR_KMS_SELECTOR)).isNotEmpty();
        assertThat(carPage.select(CAR_TRANSMISSION_SELECTOR)).isNotEmpty();
        assertThat(carPage.select(CAR_YEAR_SELECTOR)).isNotEmpty();
        assertThat(carPage.select(CAR_PRICE_SELECTOR)).isNotEmpty();
    }
}
