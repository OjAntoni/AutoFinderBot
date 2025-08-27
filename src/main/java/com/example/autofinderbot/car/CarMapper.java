package com.example.autofinderbot.car;

import com.example.autofinderbot.web.dto.car.CarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarMapper {
    CarResponse toResponse(Car car);
}
