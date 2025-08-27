package com.example.autofinderbot.car;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GearboxType {
    MANUAL("Manulana","manual"),
    AUTOMATIC("Automatyczna","automatic");

    private final String name;
    private final String searchKey;

    public static GearboxType fromSearchKey(String searchKey) {
        for (GearboxType gearboxType : values()) {
            if (gearboxType.searchKey.equals(searchKey)) {
                return gearboxType;
            }
        }
        throw new IllegalArgumentException("Unknown gearbox type: " + searchKey);
    }
}
