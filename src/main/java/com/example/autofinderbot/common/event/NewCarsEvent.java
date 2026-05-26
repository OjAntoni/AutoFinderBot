package com.example.autofinderbot.common.event;

import com.example.autofinderbot.car.core.Car;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class NewCarsEvent extends ApplicationEvent {
    private final List<Car> cars;

    public NewCarsEvent(Object source, List<Car> cars) {
        super(source);
        this.cars = cars;
    }
}
