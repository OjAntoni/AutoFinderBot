package com.example.autofinderbot.shared;

public class APIConstants {
    public static final String SEARCH_URL = "https://www.otomoto.pl/osobowe/chevrolet--dacia--ford--honda--hyundai--kia--mazda--nissan--peugeot--skoda--toyota--volkswagen/od-2005?search%5Bfilter_enum_damaged%5D=0&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_float_mileage%3Ato%5D=200000&search%5Bfilter_float_price%3Ato%5D=20000&search%5Bfilter_float_year%3Ato%5D=2014&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true";
    private static final String SEARCH_URL_WITH_PAGE = "https://www.otomoto.pl/osobowe/chevrolet--dacia--ford--honda--hyundai--kia--mazda--nissan--peugeot--skoda--toyota--volkswagen/od-2005?search%5Bfilter_enum_damaged%5D=0&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_float_mileage%3Ato%5D=200000&search%5Bfilter_float_price%3Ato%5D=20000&search%5Bfilter_float_year%3Ato%5D=2014&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true&page=";

    public static final String LISTING_JSON = "script#listing-json-ld";
    public static final String ITEM_CAR_LIST_ELEMENT = "/mainEntity/itemListElement";

    public static final String LINKS = "a[href]";
    public static final String LINK_URL = "href";

    public static final String CAR_INFO = "/itemOffered";
    public static final String PRICE_INFO = "/priceSpecification";

    public static final String NAME = "name";
    public static final String BRAND = "brand";
    public static final String FUEL_TYPE = "fuelType";
    public static final String MILEAGE = "/mileageFromOdometer/value";
    public static final String MILEAGE_TYPE = "/mileageFromOdometer/unitCode";

    public static final String PRICE = "price";
    public static final String CURRENCY = "priceCurrency";

    public static String SEARCH_URL(int page){
        return SEARCH_URL_WITH_PAGE + page;
    }

    public static final String CAR_PAGE_JSON_DATA = "script#__NEXT_DATA__";
    public static final String CAR_PAGE_ADVERT = "/props/pageProps/advert";
    public static final String CAR_PAGE_ADVERT_EQUIPMENT = "equipment";
    public static final String CAR_PAGE_ADVERT_DETAILS = "details";
    public static final String CAR_PAGE_ADVERT_CREATED_AT = "createdAt";

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String VALUES = VALUE + "s";
    public static final String LABEL = "label";
}
