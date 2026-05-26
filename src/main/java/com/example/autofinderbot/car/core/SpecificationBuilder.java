package com.example.autofinderbot.car.core;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public class SpecificationBuilder<T> {
    private Specification<T> spec = Specification.where(null);

    public <V> SpecificationBuilder<T> with(V value, Function<V, Specification<T>> specFactory) {
        if (value != null) {
            spec = spec.and(specFactory.apply(value));
        }
        return this;
    }

    public Specification<T> build() {
        return spec;
    }
}


