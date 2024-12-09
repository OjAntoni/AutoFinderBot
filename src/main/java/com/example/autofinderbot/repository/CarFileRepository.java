package com.example.autofinderbot.repository;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Repository
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CarFileRepository {
    @NonFinal
    @Value("${telegram.bot.storage.file}")
    @Getter
    String filePath;
    Set<String> carUrls = new HashSet<>();

    @PostConstruct
    private void loadData() {
        try(BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile(filePath)));) {
            String line = reader.readLine();

            while (line != null) {
                carUrls.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public boolean contains(String url) {
        return carUrls.contains(url);
    }

    public void saveUrls(Collection<String> urls) {
        File file = new File(filePath);
        FileWriter writer;

        try {
            writer = new FileWriter(file, true);
            urls.forEach(url -> {
                try {
                    carUrls.add(url);
                    writer.write(url + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
