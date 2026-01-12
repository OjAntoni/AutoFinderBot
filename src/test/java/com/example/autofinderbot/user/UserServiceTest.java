package com.example.autofinderbot.user;

import com.example.autofinderbot.car.core.Car;
import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.detail.CarDetail;
import com.example.autofinderbot.car.fuel.FuelType;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.car.seller.Seller;
import com.example.autofinderbot.configuration.BaseSpringBootTest;
import com.example.autofinderbot.filter.UserFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.autofinderbot.car.dictionary.GearboxType.AUTOMATIC;
import static com.example.autofinderbot.car.seller.Seller.SellerType.PRIVATE;
import static com.example.autofinderbot.common.util.Details.*;
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

        User savedUser = userService.save(user);

        assertThat(savedUser)
                .extracting(User::getChatId)
                .isEqualTo(123L);

        assertThat(userRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(User::getChatId)
                .isEqualTo(123L);
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
        car.setMileage(50000L);
        car.setDetails(List.of(
            new CarDetail(MODEL.name, "Corolla"),
            new CarDetail(YEAR.name, "2018"),
            new CarDetail(FUEL_TYPE.name, "Petrol"),
            new CarDetail(GENERATION.name, "e15"),
            new CarDetail(GEARBOX.name, "Automatyczna"),
            new CarDetail(DAMAGED.name, "Tak")
        ));
        car.setSeller(Seller.builder().type(PRIVATE).build());

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
        filter.setDamaged(true);
        filter.setSellerType("private");

        boolean isMatch = userService.matches(filter, car);

        assertThat(isMatch).isTrue();

    }
}
