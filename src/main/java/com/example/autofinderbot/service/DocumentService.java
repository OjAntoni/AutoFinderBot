package com.example.autofinderbot.service;

import com.example.autofinderbot.shared.Logger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;
import static org.jsoup.Jsoup.connect;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Validated
@Service
public class DocumentService {
    private static final int DEFAULT_RETRY_COUNT = 10;
    private static final int INFINITE_TIMEOUT = 0;
    private static final int INFINITE_BODY_SIZE = 0;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

    Logger logger;

    public Document load(String url, @PositiveOrZero int retryCount, Predicate<Document> validator) throws IOException {
        logger.debug("Started loading %s", url);
        Document document = null;
        boolean loadSucceeded = false;
        while (!loadSucceeded && retryCount-- > 0) {
            try {
                document = connect(url)
                        .maxBodySize(INFINITE_BODY_SIZE)
                        .timeout(INFINITE_TIMEOUT)
                        .userAgent(USER_AGENT)
                        .get();
                loadSucceeded = validator.test(document);
            } catch (UnknownHostException e) {
                logger.warn("Unknown host exception: %s", url);
            }
        }
        if(!loadSucceeded) throw new IOException("Failed to load %s".formatted(url));
        logger.debug("Ended loading %s", url);
        return document;
    }

    public Document load(String url, Predicate<Document> validator) throws IOException {
        return load(url, DEFAULT_RETRY_COUNT, validator);
    }
}
