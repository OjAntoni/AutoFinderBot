package com.example.autofinderbot.shared;

import com.example.autofinderbot.service.DocumentService;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class APIConstants {
    public static final String SEARCH_URL = "https://www.otomoto.pl/osobowe/chevrolet--dacia--ford--honda--hyundai--kia--mazda--nissan--peugeot--skoda--toyota--volkswagen/od-2005?search%5Bfilter_enum_damaged%5D=0&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_float_mileage%3Ato%5D=200000&search%5Bfilter_float_price%3Ato%5D=20000&search%5Bfilter_float_year%3Ato%5D=2014&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true";
    private static final String SEARCH_URL_WITH_PAGE = "https://www.otomoto.pl/osobowe/chevrolet--dacia--ford--honda--hyundai--kia--mazda--nissan--peugeot--skoda--toyota--volkswagen/od-2005?search%5Bfilter_enum_damaged%5D=0&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_float_mileage%3Ato%5D=200000&search%5Bfilter_float_price%3Ato%5D=20000&search%5Bfilter_float_year%3Ato%5D=2014&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true&page=";
    public static final String PAGES_SELECTOR = "div > div > div > div> div > ul > li > a > span";

    public static final String CARS_SELECTOR = "div.ooa-1340jn5 > div > div.ooa-v8iwlj.eupw8r118 > div.ooa-r53y0q.eupw8r111 > div > article > section";
    public static final String CAR_URL_SELECTOR = "article > section > div.ooa-1qo9a0p.epwfahw6 > h1 > a";
    public static final String CAR_TITLE_SELECTOR = "div.ooa-1821gv5.e12csvfg0 > div.ooa-1821gv5.e12csvfg1 > h1";
    public static final String CAR_KMS_SELECTOR = "p.e1ho6mkz2.ooa-1rcllto.er34gjf0";
    public static final String CAR_TRANSMISSION_SELECTOR = "div.ooa-sztijx.er1zng10 > div > div:nth-child(3) > p.e1ho6mkz2.ooa-1rcllto.er34gjf0";
    public static final String CAR_YEAR_SELECTOR = "main > div > aside > div.ooa-1821gv5.e12csvfg0 > div.ooa-1821gv5.e12csvfg1 > p";
    public static final String CAR_PRICE_SELECTOR = "div.ooa-1821gv5.e12csvfg0 > div.ooa-ujexr8.evnmei40 > div > div > h3";

    public static String SEARCH_URL(int page){
        return SEARCH_URL_WITH_PAGE + page;
    }

    public static void main(String[] args) throws IOException {
        DocumentService documentService = new DocumentService();
        Document doc = documentService.load("https://www.otomoto.pl/osobowe/oferta/peugeot-207-bardzo-ladny-tylko-134-000km-gwarancja-ID6GNhUV.html");
        System.out.println(doc.select(CAR_KMS_SELECTOR).first().text());
        System.out.println("....");
        System.out.println(doc.select(CAR_TITLE_SELECTOR).first().text());
        System.out.println("....");
        System.out.println(doc.select(CAR_TRANSMISSION_SELECTOR).first().text());
        System.out.println("....");
        System.out.println(doc.select(CAR_YEAR_SELECTOR).first().text().split(" · ")[1]);
        System.out.println("....");
        System.out.println(doc.select(CAR_PRICE_SELECTOR).first().text());
        System.out.println("....");
    }
}
