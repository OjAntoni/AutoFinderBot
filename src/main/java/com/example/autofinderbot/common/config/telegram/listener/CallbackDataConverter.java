package com.example.autofinderbot.common.config.telegram.listener;

import com.example.autofinderbot.common.util.Parameter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallbackDataConverter {

    public String convert(String command, Parameter... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("tg_c=%s".formatted(command));
        for (Parameter param : args) {
            sb.append(";%s=%s".formatted(param.getName(), param.getValue()));
        }
        return sb.toString();
    }

    public Map<String, Object> convert(String callbackData) {
        String[] parts = callbackData.split(";");
        HashMap<String, Object> map = new HashMap<>(parts.length);

        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid callback data format: " + part);
            }
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }
}
