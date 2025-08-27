package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.parser.service.DocumentService;
import jakarta.validation.ConstraintViolationException;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.autofinderbot.common.util.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OtomotoDocumentServiceTest extends BaseSpringBootTest {
    @Autowired
    DocumentService<Document> documentService;

    @Test
    void loadDocument_PosTC() throws IOException {
        assertThat(documentService.load(SEARCH_URL, 15, (document) -> true))
            .isNotNull();
    }

    @Test
    void loadDocumentWithRetry_PosTC() throws IOException {
        final AtomicInteger counter = new AtomicInteger(1);

        assertThat(documentService.load(SEARCH_URL, (document) -> counter.getAndIncrement() == 10))
            .isNotNull();
    }

    @Test
    void throwOnExceededRetryCount_NegTC() throws IOException {
        final AtomicInteger counter = new AtomicInteger(1);

        assertThatThrownBy(() -> documentService.load(SEARCH_URL, 5, (document) -> counter.getAndIncrement() == 6))
            .isInstanceOf(IOException.class);
    }

    @Test
    void throwOnLoadFileWithNegativeRetries_NegTC() {
        assertThatThrownBy(() -> documentService.load("", -1, (document) -> true))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @RepeatedTest(5)
    void loadDocumentWithDefaultRetryCount_PosTC() {
        assertDoesNotThrow(() -> documentService.load(SEARCH_URL, (document) -> true));
    }
}