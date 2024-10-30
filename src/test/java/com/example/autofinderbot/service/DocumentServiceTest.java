package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.example.autofinderbot.shared.APIConstants.SEARCH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceTest extends BaseSpringBootTest {

    @Autowired
    DocumentService documentService;

    @Test
    void loadDocument_PosTC() throws IOException {
        assertThat(documentService.load(SEARCH_URL, 15))
            .isNotNull();
    }

    @Test
    void throwOnLoadFileWithNegativeRetries_NegTC() {
        assertThatThrownBy(() -> documentService.load("", -1))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @RepeatedTest(5)
    void loadDocumentWithDefaultRetryCount_PosTC() {
        assertDoesNotThrow(() -> documentService.load(SEARCH_URL));
    }
}
