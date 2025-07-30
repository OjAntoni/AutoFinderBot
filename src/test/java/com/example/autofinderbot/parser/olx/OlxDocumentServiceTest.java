package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.parser.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class OlxDocumentServiceTest extends BaseSpringBootTest {

    @Autowired
    DocumentService<String> documentService;

    @Test
    void urlIsValid_PosTC() {
        String url = "https://www.olx.pl/motoryzacja/samochody/";
        assertTrue(documentService.isValid(url));
    }

    @Test
    void urlIsInvalid_PosTC() {
        String url = "https://www.olx.pl/motoryzacja/xxx/";
        assertFalse(documentService.isValid(url));
    }
}