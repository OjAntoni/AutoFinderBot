package com.example.autofinderbot.telegram.converter;

import com.example.autofinderbot.domain.GearboxType;
import com.example.autofinderbot.domain.UserFilter;
import com.example.autofinderbot.service.CarFiltersService;
import com.example.autofinderbot.common.util.Logger;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UrlToFiltersConverter {
    CarFiltersService carFiltersService;
    Logger logger;

    public UserFilter parseUrl(String urlString) {
        Map<String, Object> result = new HashMap<>();
        UserFilter userFilter = new UserFilter();

        try {
            URI uri = new URI(urlString);
            String path = uri.getPath();
            String query = uri.getQuery();

            // Extract car brands from the path
            List<String> brands = extractBrands(path);
            userFilter.setCarBrands(carFiltersService.getBrands(brands));
            result.put("brands", brands);

            // Extract car models from the path
            String[] pathSegments = path.split("/");
            List<String> models = extractModels(pathSegments);
            result.put("models", extractModels(pathSegments));
            userFilter.setCarModels(carFiltersService.getModels(models));

            // Extract price range
            String priceStart = extractFromQuery(query, "filter_float_price:from");
            result.put("price_start", priceStart);
            String priceEnd = extractFromQuery(query, "filter_float_price:to");
            result.put("price_end", priceEnd);
            userFilter.setPriceStart(priceStart == null ? null : Long.parseLong(priceStart));
            userFilter.setPriceEnd(priceEnd == null ? null : Long.parseLong(priceEnd));

            // Extract year range
            String yearFrom = extractYearFrom(pathSegments);
            result.put("year_from", yearFrom);
            String yearTo = extractFromQuery(query, "filter_float_year:to");
            result.put("year_to", yearTo);
            userFilter.setYearFrom(yearFrom == null ? null : Integer.parseInt(yearFrom));
            userFilter.setYearTo(yearTo == null ? null : Integer.parseInt(yearTo));

            // Extract mileage range
            String mileageFrom = extractFromQuery(query, "filter_float_mileage:from");
            result.put("mileage_from", mileageFrom);
            String mileageTo = extractFromQuery(query, "filter_float_mileage:to");
            result.put("mileage_to", mileageTo);
            userFilter.setMileageFrom(mileageFrom == null ? null : Integer.parseInt(mileageFrom));
            userFilter.setMileageTo(mileageTo == null ? null : Integer.parseInt(mileageTo));

            // Extract fuel types
            List<String> fuelTypes = extractMultipleFromQuery(query, "filter_enum_fuel_type");
            result.put("fuel_types", fuelTypes);
            userFilter.setFuelTypes(carFiltersService.getFuelTypes(fuelTypes));

            // Extract generations
            List<String> generations = extractMultipleFromQuery(query, "filter_enum_generation");
            result.put("generations", generations);
            userFilter.setGenerations(carFiltersService.getGenerations(generations));

            // Extract gearbox
            List<String> gearboxes = extractMultipleFromQuery(query, "filter_enum_gearbox");
            List<GearboxType> gearboxTypes = gearboxes.stream().map(GearboxType::fromSearchKey).toList();
            result.put("gearboxes", gearboxTypes);
            userFilter.setGearboxes(gearboxTypes);

            // Extract damaged state
            String damagedValue = extractFromQuery(query, "filter_enum_damaged");
            Boolean damaged = null;
            if (damagedValue != null) {
                damaged = damagedValue.equals("1");
            }
            result.put("damaged", damaged);
            userFilter.setDamaged(damaged);

            // Extract seller type
            String sellerType = extractFromQuery(query, "private_business");
            result.put("seller_type", sellerType);
            userFilter.setSellerType(sellerType);

        } catch (Exception e) {
            logger.error(e);
        }

        return userFilter;
    }

    private static List<String> extractBrands(String path) {
        Pattern brandPattern = Pattern.compile("/osobowe/([^/]+)");
        Matcher matcher = brandPattern.matcher(path);
        if (matcher.find()) {
            String brandsPart = matcher.group(1);
            return Arrays.asList(brandsPart.split("--"));
        }
        return Collections.emptyList();
    }

    private static List<String> extractModels(String[] pathSegments) {
        if (pathSegments.length > 3) {
            return Arrays.asList(pathSegments[3].split("--"));
        }
        return Collections.emptyList();
    }

    private static String extractYearFrom(String[] pathSegments) {
        for (String pathSegment : pathSegments) {
            if (pathSegment.matches("od-\\d{4}")) {
                return pathSegment.substring(3);
            }
        }
        return null;
    }

    private static String extractFromQuery(String query, String key) {
        try {
            String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("search\\[" + Pattern.quote(key) + "\\]=([^&]+)");
            Matcher matcher = pattern.matcher(decodedQuery);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("Error decoding query: " + e.getMessage());
        }
        return null;
    }

    private static List<String> extractMultipleFromQuery(String query, String key) {
        List<String> values = new ArrayList<>();
        try {
            String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("search\\[" + Pattern.quote(key) + "\\](?:\\[\\d+\\])?=([^&]+)");
            Matcher matcher = pattern.matcher(decodedQuery);
            while (matcher.find()) {
                values.add(matcher.group(1));
            }
        } catch (Exception e) {
            System.err.println("Error decoding query: " + e.getMessage());
        }
        return values;
    }
}
