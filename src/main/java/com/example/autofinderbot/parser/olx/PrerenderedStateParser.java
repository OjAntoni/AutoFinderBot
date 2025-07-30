package com.example.autofinderbot.parser.olx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PrerenderedStateParser {

    private static final Pattern STATE_PATTERN = Pattern.compile(
        "window\\.__PRERENDERED_STATE__\\s*=\\s*(\"[\\s\\S]*?\")\\s*;"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Extracts and parses the JSON payload from the JS assignment:
     *   window.__PRERENDERED_STATE__= "{ ... }";
     * into a Jackson JsonNode, with all unicode escapes un-escaped.
     *
     * @param html the full page HTML (or script contents)
     * @return the parsed JSON tree
     * @throws IOException if parsing fails
     */
    public JsonNode parse(String html) throws IOException {
        Matcher m = STATE_PATTERN.matcher(html);
        if (!m.find()) {
            throw new IllegalStateException("No __PRERENDERED_STATE__ assignment found");
        }

        // 1) Grab the entire quoted JS literal, including its double-quotes
        String jsLiteral = m.group(1);

        // 2) Unescape it to raw JSON. Jackson sees it as a JSON string → returns the inner text
        String rawJson = mapper.readValue(jsLiteral, String.class);

        // 3) Parse that raw JSON into a tree
        return mapper.readTree(rawJson);
    }
}
