package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.CarResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.autofinderbot.shared.APIConstants.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Service
public class CarService {
    DocumentService documentService;

    public int getPages(Document document) {
        Elements pages = document.select(PAGES_SELECTOR);
        return Integer.parseInt(pages.get(pages.size() - 1).text());
    }

    public List<CarResponse> findCars(Document document) throws IOException {
        List<CarResponse> carResponses = new ArrayList<>();
        Elements elements = document.select(CAR_URL_SELECTOR);
        System.out.println(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            String carUrl = elements.get(i).attr("href");
            Document carDocument = documentService.load(carUrl);
            String carPrice = getProperty(carDocument, CAR_PRICE_SELECTOR);
            String carKms = getProperty(carDocument, CAR_KMS_SELECTOR);
            String carTransmission = getProperty(carDocument, CAR_TRANSMISSION_SELECTOR);
            String carYear = getYear(carDocument);
            String carTitle = getProperty(carDocument, CAR_TITLE_SELECTOR);
            carResponses.add(new CarResponse(carTitle, carKms, carTransmission, carYear, carPrice, carUrl));
        }
        return carResponses;
    }

    private String getProperty(Document doc, String selector) {
        Elements elements = doc.select(selector);
        if(!elements.isEmpty()) {
            return elements.get(0).text();
        }
        return null;
    }

    private String getYear(Document doc) {
        Elements elements = doc.select(CAR_YEAR_SELECTOR);
        if(!elements.isEmpty()) {
            return elements.get(0).text().split(" · ")[1];
        }
        return null;
    }

    public String formatCarResponse(CarResponse car) {
        return  "🚗 " + car.title() + "\n" +
                "🛞 Kilometers: " + car.kms() + "\n" +
                "⚙️ Transmission: " + car.transmission() + "\n" +
                "📅 Year: " + car.year() + "\n" +
                "💵 Price: " + car.price() + "\n" +
                "🔗 Link " + car.url();
    }
}
