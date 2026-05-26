package com.example.autofinderbot.common.util;

public class APIConstants {
    public static final String OLX_URL = "https://www.olx.pl/";
    public static final String OLX_SEARCH_URL = "https://www.olx.pl/motoryzacja/samochody/?search%5Border%5D=created_at%3Adesc";
    private static final String OLX_SEARCH_URL_WITH_PAGE = OLX_SEARCH_URL + "&page=";
    public static final String OTOMOTO_URL = "https://www.otomoto.pl/";
    public static final String OTOMOTO_FILTERS_URL = OTOMOTO_URL + "osobowe";
    public static final String SEARCH_URL = "https://www.otomoto.pl/osobowe?search%5Border%5D=created_at_first%3Adesc";
    private static final String SEARCH_URL_WITH_PAGE = SEARCH_URL + "&page=";

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

    public static String OLX_SEARCH_URL(int page){
        return OLX_SEARCH_URL_WITH_PAGE + page;
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

    public static final String CAR_FILTERS = "/props/pageProps/filters";
    public static final String CAR_BRANDS = "/571/_meta/values";
    public static final String CAR_MODELS = "/props/pageProps/filtersValues";
    public static final String FUEL_TYPES = "/581/_meta/values";

    public static final String GROUP_VALUES = "group_values";
    public static final String VALUE_KEY = "value_key";
    public static final String SEARCH_KEY = "search_key";
}
