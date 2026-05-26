package com.example.autofinderbot.car.seller;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@Builder
public class Seller {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;
    String name;
    @Enumerated(STRING)
    SellerType type;
    @Embedded
    Address address;

    @AllArgsConstructor
    @Getter
    public enum SellerType {
        PRIVATE("Prywatne", "private"),
        PROFESSIONAL("Firma", "business");

        private final String name;
        private final String searchKey;

        public static SellerType fromSearchKey(String searchKey) {
            for (SellerType sellerType : values()) {
                if (sellerType.searchKey.equals(searchKey)) {
                    return sellerType;
                }
            }
            throw new IllegalArgumentException("Unknown seller type: " + searchKey);
        }
    }
}
