package com.example.autofinderbot.parser.service;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * Service interface for loading and validating documents of type T.
 *
 * @param <T> the type of document to be loaded and validated
 */
@Validated
public interface DocumentService<T> {
    /**
     * Loads content of type T from the given URL, retrying up to {@code retryCount} times
     * until {@code validator.test(content)} returns true.
     *
     * @param url the URL to load the document from
     * @param retryCount the number of times to retry loading if validation fails
     * @param validator a predicate to validate the loaded content
     * @return the loaded and validated content of type T
     * @throws IOException if an I/O error occurs during loading
     */
    T load(String url, @PositiveOrZero int retryCount, Predicate<T> validator) throws IOException;

    /**
     * Loads content of type T from the given URL with no retries,
     * until {@code validator.test(content)} returns true.
     *
     * @param url the URL to load the document from
     * @param validator a predicate to validate the loaded content
     * @return the loaded and validated content of type T
     * @throws IOException if an I/O error occurs during loading
     */
    default T load(String url, Predicate<T> validator) throws IOException {
        return load(url, 1, validator);
    }

    /**
     * Checks if the given url is valid.
     *
     * @param url the url to validate
     * @return {@code true} if the url is valid, {@code false} otherwise
     */
    boolean isValid(String url);
}
