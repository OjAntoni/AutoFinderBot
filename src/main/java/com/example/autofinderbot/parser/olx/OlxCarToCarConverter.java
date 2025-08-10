package com.example.autofinderbot.parser.olx;

import com.example.autofinderbot.domain.*;
import com.example.autofinderbot.repository.CarBrandRepository;
import com.example.autofinderbot.repository.CarModelRepository;
import com.example.autofinderbot.repository.FuelTypeRepository;
import com.example.autofinderbot.shared.DateTimeUtil;
import com.example.autofinderbot.shared.Details;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OlxCarToCarConverter {
    OlxCarParamsToCarDetailsConverter olxCarParamsToCarDetailsConverter;
    FuelTypeRepository fuelTypeRepository;
    CarModelRepository carModelRepository;
    CarBrandRepository carBrandRepository;
    DateTimeUtil dateTimeUtil;
    @NonFinal
    List<FuelType> fuelTypes;

    public Car convert(OlxCar olxCar) {
        Car car = new Car();

        car.setTitle(olxCar.getTitle());
        car.setFuelType(getFuelType(olxCar).map(FuelType::getName).orElse(null));
        car.setMileage(getMileage(olxCar).orElse(null));
        car.setMileageUnit("KMT");
        car.setPrice(olxCar.getPrice().getRegularPrice().getValue());
        car.setCurrency(olxCar.getPrice().getRegularPrice().getCurrencyCode());
        car.setUrl(olxCar.getExternalUrl() != null ? olxCar.getExternalUrl() : olxCar.getUrl());
        car.setCreatedAt(dateTimeUtil.convert(olxCar.getCreatedTime()));
        car.setSeller(getSeller(olxCar));
        car.setDescription(olxCar.getDescription());
        car.setImageUrls(olxCar.getPhotos());
        car.setThumbnailUrl(olxCar.getPhotos().isEmpty() ? null : olxCar.getPhotos().getFirst());

        List<CarDetail> details = olxCarParamsToCarDetailsConverter.convert(olxCar.getParams());

        car.setDetails(details);
        car.setBrand(getBrand(details, olxCar.getTitle()));
        car.setSource(Car.Source.OLX);

        return car;
    }

    private Optional<FuelType> getFuelType(OlxCar olxCar) {
        if(fuelTypes == null) {
            fuelTypes = fuelTypeRepository.findAll();
        }

        OlxCar.Param fuelTypeParam = get("petrol", olxCar);
        if (fuelTypeParam == null) {
            return Optional.empty();
        }

        String fuelTypeName = fuelTypeParam.getValue();

        List<FuelType> matchingFuels = fuelTypes.stream().filter(fuel -> StringUtils.containsIgnoreCase(fuel.getName(), fuelTypeName)).toList();

        return Optional.ofNullable(matchingFuels.isEmpty() ? null : matchingFuels.getFirst());
    }

    private Optional<Long> getMileage(OlxCar olxCar) {
        OlxCar.Param mileageParam = get("milage", olxCar);
        if (mileageParam == null) {
            return Optional.empty();
        }

        String mileageValue = mileageParam.getNormalizedValue();
        try {
            return Optional.of(Long.parseLong(mileageValue.replaceAll("\\D", "")));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Seller getSeller(OlxCar olxCar) {
        OlxCar.User user = olxCar.getUser();

        return Seller.builder()
            .name(user.getName())
            .type(olxCar.isBusiness() ? Seller.SellerType.PROFESSIONAL : Seller.SellerType.PRIVATE)
            .address(getAddress(olxCar))
            .build();
    }

    private Address getAddress(OlxCar olxCar) {
        return Address.builder()
            .address(olxCar.getLocation().getPathName())
            .city(olxCar.getLocation().getCityName())
            .cityId(olxCar.getLocation().getCityId())
            .region(olxCar.getLocation().getRegionName())
            .regionId((long) olxCar.getLocation().getRegionId())
            .shortAddress(olxCar.getLocation().getPathName())
            .latitude(olxCar.getMapLocation().getLat())
            .longitude(olxCar.getMapLocation().getLon())
            .build();
    }

    private String getBrand(List<CarDetail> carDetails, String title) {
        CarDetail model = carDetails.stream()
            .filter(cd -> cd.getDetail().equalsIgnoreCase(Details.MODEL.attribute))
            .findFirst()
            .orElse(null);

        if (model == null) {
            return null;
        }

        List<CarModel> carModels = carModelRepository.findAllByName(model.getValue());

        if (carModels.isEmpty()) {
            return null;
        }
        if (carModels.size() == 1) {
            return carBrandRepository.findById(carModels.getFirst().getCarBrandId())
                .map(CarBrand::getName)
                .orElse(null);
        }

        Map<Long, String> carBrandIdToCarBrand = carBrandRepository.findAllById(carModels.stream().map(CarModel::getCarBrandId).toList())
            .stream()
            .collect(Collectors.toMap(CarBrand::getId, CarBrand::getName));

        for (CarModel carModel : carModels) {
            if(StringUtils.containsIgnoreCase(title, carModel.getName())) {
                String brandName = carBrandIdToCarBrand.get(carModel.getCarBrandId());
                if (brandName != null) {
                    return brandName;
                }
            }
        }

        return null;
    }

    private OlxCar.Param get(String key, OlxCar olxCar) {
        return olxCar.getParams().stream()
                .filter(param -> key.equals(param.getKey()))
                .findFirst()
                .orElse(null);
    }
}

