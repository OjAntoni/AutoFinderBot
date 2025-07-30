package com.example.autofinderbot.parser.olx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OlxCar {
    private long id;
    private String title;
    private String description;

    private Category category;

    @JsonProperty("map")
    private MapLocation mapLocation;

    @JsonProperty("isBusiness")
    private boolean business;

    private String url;

    @JsonProperty("isHighlighted")
    private boolean highlighted;

    @JsonProperty("isPromoted")
    private boolean promoted;

    private Promotion promotion;
    private String externalUrl;
    private Delivery delivery;

    private OffsetDateTime createdTime;
    private OffsetDateTime lastRefreshTime;
    private OffsetDateTime pushupTime;
    private OffsetDateTime validToTime;

    @JsonProperty("isActive")
    private boolean active;

    private String status;
    private List<Param> params;
    private String itemCondition;
    private Price price;
    private Object salary;
    private Partner partner;

    @JsonProperty("isJob")
    private boolean job;

    private List<String> photos;
    private List<String> photosSet;
    private Location location;
    private String urlPath;
    private Contact contact;
    private User user;
    private Shop shop;
    private Safedeal safedeal;
    private String searchReason;

    @JsonProperty("isNewFavouriteAd")
    private boolean newFavouriteAd;

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        private long id;
        private String type;
        @JsonProperty("_nodeId")
        private String nodeId;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapLocation {
        private double lat;
        private double lon;
        private int radius;
        @JsonProperty("show_detailed")
        private boolean showDetailed;
        private int zoom;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Promotion {
        private boolean highlighted;
        @JsonProperty("top_ad")
        private boolean topAd;
        private List<String> options;
        @JsonProperty("premium_ad_page")
        private boolean premiumAdPage;
        private boolean urgent;
        @JsonProperty("b2c_ad_page")
        private boolean b2cAdPage;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Delivery {
        private Rock rock;
    }
    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rock {
        private boolean active;
        private String mode;
        @JsonProperty("offer_id")
        private Long offerId;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Param {
        private String key;
        private String name;
        private String type;
        private String value;
        private String normalizedValue;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {
        private boolean budget;
        private boolean free;
        private boolean exchange;
        private String displayValue;
        @JsonProperty("regularPrice")
        private RegularPrice regularPrice;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RegularPrice {
        private double value;
        private String currencyCode;
        private String currencySymbol;
        private boolean negotiable;
        private PriceFormatConfig priceFormatConfig;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceFormatConfig {
        private String decimalSeparator;
        private String thousandsSeparator;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Partner {
        private String code;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String cityName;
        private long cityId;
        private String cityNormalizedName;
        private String regionName;
        private int regionId;
        private String regionNormalizedName;
        private String districtName;
        private int districtId;
        private String pathName;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        private boolean chat;
        private boolean courier;
        private String name;
        private boolean negotiation;
        private boolean phone;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private long id;
        private String name;
        private String photo;
        private String logo;
        private boolean otherAdsEnabled;
        private String socialNetworkAccountType;
        private boolean isOnline;
        private OffsetDateTime lastSeen;
        private String about;
        private String bannerDesktopURL;
        private String logoAdPage;
        private String companyName;
        private OffsetDateTime created;
        private String sellerType;
        private String uuid;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Shop {
        private String subdomain;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Safedeal {
        @JsonProperty("allowed_quantity")
        private List<Object> allowedQuantity;
        @JsonProperty("weight_grams")
        private int weightGrams;
    }
}

