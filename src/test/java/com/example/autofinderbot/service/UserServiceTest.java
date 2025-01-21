package com.example.autofinderbot.service;

import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.domain.*;
import com.example.autofinderbot.repository.UserRepository;
import com.example.autofinderbot.shared.Details;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.autofinderbot.domain.GearboxType.AUTOMATIC;
import static com.example.autofinderbot.shared.Details.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends BaseSpringBootTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void save_PosTC(){
        User user = new User();
        user.setChatId(123L);
        user.setSearchUrl("https://example.com");


        User savedUser = userService.save(user);

        assertThat(savedUser)
                .extracting(User::getChatId, User::getSearchUrl)
                .containsExactly(123L, "https://example.com");

        assertThat(userRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(User::getChatId, User::getSearchUrl)
                .containsExactly(123L, "https://example.com");
    }

    @Test
    void delete_PosTC() {
        userService.delete(3L);

        assertThat(userRepository.findById(3L))
                .isEmpty();
    }

    @Test
    void filterMatch_PosTC(){
        Car car = new Car();
        car.setBrand("Toyota");
        car.setPrice(15000);
        car.setMileage(50000);
        car.setDetails(List.of(
            new CarDetail(MODEL.name, "Corolla"),
            new CarDetail(YEAR.name, "2018"),
            new CarDetail(FUEL_TYPE.name, "Petrol"),
            new CarDetail(GENERATION.name, "e15"),
            new CarDetail(GEARBOX.name, "Automatyczna")
        ));

        UserFilter filter = new UserFilter();
        filter.setCarBrands(List.of(new CarBrand(1L, "Toyota", "Toyota")));
        filter.setPriceStart(10000L);
        filter.setPriceEnd(20000L);
        filter.setMileageFrom(40000);
        filter.setMileageTo(60000);
        filter.setYearFrom(2015);
        filter.setYearTo(2020);
        filter.setCarModels(List.of(new CarModel(1L, "Corolla", "Corolla", 1L)));
        filter.setFuelTypes(List.of(new FuelType(1L, "Petrol", "Petrol")));
        filter.setGenerations(List.of(new Generation(1L, "e15", "e15", 1L)));
        filter.setGearboxes(List.of(AUTOMATIC));

        boolean isMatch = userService.matches(filter, car);

        assertThat(isMatch).isTrue();

    }
}
