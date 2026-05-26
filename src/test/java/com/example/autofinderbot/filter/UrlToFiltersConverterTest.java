package com.example.autofinderbot.filter;

import com.example.autofinderbot.car.brand.CarBrand;
import com.example.autofinderbot.car.model.CarModel;
import com.example.autofinderbot.car.fuel.FuelType;
import com.example.autofinderbot.car.generation.Generation;
import com.example.autofinderbot.configuration.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.autofinderbot.car.dictionary.GearboxType.MANUAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UrlToFiltersConverterTest extends BaseSpringBootTest {
    @Autowired
    private UrlToFiltersConverter urlToFiltersConverter;

    @Test
    void parseUrl_PosTC() {
        String url = "https://www.otomoto.pl/osobowe/audi/a4/seg-coupe/od-2012/biadacz-kamienisko?search%5Bdist%5D" +
                "=50&search%5Bfilter_enum_360_view_camera%5D=1&search%5Bfilter_enum_color%5D=brown-beige&search%5Bfil" +
                "ter_enum_country_origin%5D=by&search%5Bfilter_enum_damaged%5D=0&search%5Bfilter_enum_door_count%5D%5B" +
                "0%5D=3&search%5Bfilter_enum_door_count%5D%5B1%5D=5&search%5Bfilter_enum_fuel_type%5D%5B0%5D=petrol&se" +
                "arch%5Bfilter_enum_fuel_type%5D%5B1%5D=diesel&search%5Bfilter_enum_gearbox%5D=manual&search%5Bfilter_" +
                "enum_generation%5D=gen-b6-2000-2004&search%5Bfilter_enum_memory_seat%5D=1&search%5Bfilter_enum_servic" +
                "e_record%5D=1&search%5Bfilter_enum_sunroof%5D=glass-sunroof-fixed&search%5Bfilter_float_mileage%3Afro" +
                "m%5D=20000&search%5Bfilter_float_mileage%3Ato%5D=175000&search%5Bfilter_float_nr_seats%5D=4&search%5B" +
                "filter_float_price%3Afrom%5D=30000&search%5Bfilter_float_price%3Ato%5D=67000&search%5Bfilter_float_ye" +
                "ar%3Ato%5D=2023&search%5Bprivate_business%5D=business&search%5Badvanced_search_expanded%5D=true";

        UserFilter filter = urlToFiltersConverter.parseUrl(url);

        assertThat(filter)
            .extracting(
                UserFilter::isConfirmed,
                UserFilter::getMileageFrom,
                UserFilter::getMileageTo,
                UserFilter::getYearFrom,
                UserFilter::getYearTo,
                UserFilter::getPriceStart,
                UserFilter::getPriceEnd,
                UserFilter::getDamaged,
                UserFilter::getSellerType,
                f -> f.getCarBrands().stream().map(CarBrand::getName).toList(),
                f -> f.getCarModels().stream().map(CarModel::getName).toList(),
                f -> f.getGenerations().stream().map(Generation::getName).toList(),
                f -> f.getFuelTypes().stream().map(FuelType::getName).toList(),
                f -> f.getGearboxes().stream().toList()
            ).containsExactly(
            false,
                20000,
                175000,
                2012,
                2023,
                30000L,
                67000L,
                false,
                "business",
                List.of("Audi"),
                List.of("A4"),
                List.of("B6 (2000-2004)"),
                List.of("Benzyna", "Diesel"),
                List.of(MANUAL));
    }

}