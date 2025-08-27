package com.example.autofinderbot.car.converter;

import com.example.autofinderbot.car.Car;
import com.example.autofinderbot.car.detail.CarDetail;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CarDetailsConverter {

    public String convert(Car car) {
        List<CarDetail> details = car.getDetails();
        StringBuilder sb = new StringBuilder();
        for (CarDetail detail : details) {
            sb.append(detail.getDetail()).append(": ").append(detail.getValue()).append("\n");
        }
        return sb.toString();
    }
}
