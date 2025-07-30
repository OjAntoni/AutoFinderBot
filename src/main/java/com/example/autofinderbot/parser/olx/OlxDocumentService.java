package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.parser.service.DocumentService;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;

@Service
@Qualifier("olx")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class OlxDocumentService implements DocumentService<String> {
    private static final String USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    Playwright playwright;
    Browser browser;
    BrowserContext context;

    @PostConstruct
    private void init() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true)
            .setArgs(List.of("--disable-blink-features=AutomationControlled"))
        );
        context = browser.newContext(new Browser.NewContextOptions()
            .setUserAgent(USER_AGENT)
            .setLocale("pl-PL")
            .setExtraHTTPHeaders(Map.of(
                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Accept-Language", "pl-PL,pl;q=0.9,en-US;q=0.8",
                "Referer", "https://www.olx.pl/"
            ))
            .setBypassCSP(true)
        );
    }

    @PreDestroy
    public void shutdown() {
        browser.close();
        playwright.close();
    }

    @Override
    public String load(String url, int retryCount, Predicate<String> validator) throws IOException {
        IOException last = null;
        while (retryCount-- > 0) {
            try {
                String html = fetchRawHtml(url);
                if (validator.test(html)) {
                    return html;
                }
            } catch (Exception e) {
                last = new IOException(e);
            }
        }
        throw new IOException("Failed to load OLX page: " + url, last);
    }

    private String fetchRawHtml(String url) {
        Page page = context.newPage();
        AtomicReference<Response> ref = new AtomicReference<>();

        page.onResponse(r -> {
            if ("document".equals(r.request().resourceType()) && r.url().contains("/motoryzacja/samochody/")) {
                ref.set(r);
            }
        });

        page.navigate(url, new Page.NavigateOptions()
            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        Response resp = ref.get();

        String rawHtml = resp != null ?resp.text() : "";

        page.close();

        return rawHtml;
    }

    @Override
    public boolean isValid(String url) {
        try {
            Page page = context.newPage();
            Response resp = page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            int status = resp.status();
            page.close();

            return status / 100 != 4 && status / 100 != 5;
        } catch (PlaywrightException e) {
            return false;
        }
    }
}
