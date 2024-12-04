package com.example.autofinderbot.domain;

public record CarResponse(
        String title,
        String kms,
        String transmission,
        String year,
        String price,
        String url) {
}
