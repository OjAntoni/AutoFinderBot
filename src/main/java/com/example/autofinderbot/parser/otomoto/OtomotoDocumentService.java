package com.example.autofinderbot.parser.otomoto;

import com.example.autofinderbot.parser.service.DocumentService;
import com.example.autofinderbot.shared.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;
import static org.jsoup.Jsoup.connect;

@Service
@Qualifier("otomoto")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OtomotoDocumentService implements DocumentService<Document> {
    private static final int DEFAULT_RETRY_COUNT = 10;
    private static final int INFINITE_TIMEOUT = 0;
    private static final int INFINITE_BODY_SIZE = 0;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

    Logger logger;

    @Override
    public Document load(String url, int retryCount, Predicate<Document> validator) throws IOException {
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

    @Override
    public Document load(String url, Predicate<Document> validator) throws IOException {
        return load(url, DEFAULT_RETRY_COUNT, validator);
    }

    @Override
    public boolean isValid(String url) {
        try {
            int code = connect(url)
                .ignoreHttpErrors(true)
                .execute()
                .statusCode();
            return code / 100 != 4 && code / 100 != 5;
        } catch (IOException e) {
            return false;
        }
    }
}
