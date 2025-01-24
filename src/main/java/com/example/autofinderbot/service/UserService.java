package com.example.autofinderbot.service;

import com.example.autofinderbot.domain.*;
import com.example.autofinderbot.repository.UserFilterRepository;
import com.example.autofinderbot.repository.UserRepository;
import com.example.autofinderbot.shared.Details;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.example.autofinderbot.domain.UserFilter.State.NEW;
import static com.example.autofinderbot.domain.UserFilter.State.OLD;
import static com.example.autofinderbot.shared.Answer.NO_PL;
import static com.example.autofinderbot.shared.Answer.YES_PL;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserFilterRepository userFilterRepository;

    @Transactional
    public User save(User user){
        return userRepository.save(user);
    }

    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User findByChatId(long chatId) {
        return userRepository.getByChatId(chatId);
    }

    @Transactional
    public UserFilter save(UserFilter userFilter) {
        if (userFilterRepository.findById(userFilter.getId()).isEmpty() && userFilterRepository.existsByUser_IdAndState(userFilter.getUser().getId(), NEW)) {
            UserFilter oldFilter = userFilterRepository.findByUser_Id(userFilter.getUser().getId());
            oldFilter.setState(OLD);
        }
        userFilter.setState(NEW);
        return userFilterRepository.save(userFilter);
    }

    @Transactional
    public void removeOldFilter(User user) {
        userFilterRepository.deleteByUser_IdAndState(user.getId(), OLD);
    }

    @Transactional
    public void rollbackToOldFilter(User user) {
        UserFilter newFilter = userFilterRepository.findByUser_IdAndState(user.getId(), NEW);
        userFilterRepository.deleteById(newFilter.getId());
        UserFilter oldFilter = userFilterRepository.findByUser_IdAndState(user.getId(), OLD);
        if (oldFilter == null) return;
        oldFilter.setState(NEW);
    }

    @Transactional(readOnly = true)
    public UserFilter findUserFilter(long userId) {
        return userFilterRepository.findByUser_IdAndState(userId, NEW);
    }

    @Transactional(readOnly = true)
    public boolean existsByChatId(Long chatId) {
        return userRepository.existsByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public List<UserFilter> findAllUserFilters() {
        return userFilterRepository.findAllByConfirmedIs(true);
    }

    public boolean matches(UserFilter filter, Car car) {
        Map<String, String> details = car.getDetails().stream()
                .collect(toMap(CarDetail::getDetail, CarDetail::getValue));

        return brandMatch(filter, car) &&
                modelMatch(filter, details) &&
                generationMatch(filter, details) &&
                priceMatch(filter, car) &&
                yearMatch(filter, details) &&
                mileageMatch(filter, car) &&
                fuelTypeMatch(filter, details) &&
                gearboxTypeMatch(filter, details) &&
                damagedMatch(filter, details);
    }

    private boolean brandMatch(UserFilter filter, Car car) {
        List<CarBrand> filterBrands = filter.getCarBrands();
        if(filterBrands == null || filterBrands.isEmpty()) return true;
        return filterBrands.stream()
                .map(CarBrand::getName)
                .anyMatch(brandName -> brandName.equals(car.getBrand()));
    }

    private boolean modelMatch(UserFilter filter, Map<String, String> details) {
        List<CarModel> filterModels = filter.getCarModels();
        if(filterModels == null || filterModels.isEmpty()) return true;
        String model = details.get(Details.MODEL.name);
        return model != null && filterModels.stream()
                .map(CarModel::getName)
                .anyMatch(modelName -> modelName.equals(model));
    }

    private boolean generationMatch(UserFilter filter, Map<String, String> details) {
        List<Generation> filterGenerations = filter.getGenerations();
        if(filterGenerations == null || filterGenerations.isEmpty()) return true;
        String generation = details.get(Details.GENERATION.name);
        return generation != null && filterGenerations.stream()
                .map(Generation::getName)
                .anyMatch(modelName -> modelName.equals(generation));
    }

    private boolean priceMatch(UserFilter filter, Car car) {
        double carPrice = car.getPrice();
        long priceStart = filter.getPriceStart() == null ? 0 : filter.getPriceStart();
        long priceEnd = filter.getPriceEnd() == null ? Long.MAX_VALUE : filter.getPriceEnd();
        return carPrice >= priceStart && carPrice <= priceEnd;
    }

    private boolean yearMatch(UserFilter filter, Map<String, String> details) {
        if (filter.getYearFrom() == null && filter.getYearTo() == null) return true;
        String yearStr = details.get(Details.YEAR.name);
        if(yearStr == null) return false;
        int carYear = Integer.parseInt(yearStr);
        long yearFrom = filter.getYearFrom() == null ? 0 : filter.getYearFrom();
        long yearTo = filter.getYearTo() == null ? Integer.MAX_VALUE : filter.getYearTo();
        return carYear >= yearFrom && carYear <= yearTo;
    }

    private boolean mileageMatch(UserFilter filter, Car car) {
        long carMileage = car.getMileage();
        int mileageFrom = filter.getMileageFrom() == null ? 0 : filter.getMileageFrom();
        int mileageTo = filter.getMileageTo() == null ? Integer.MAX_VALUE : filter.getMileageTo();
        return carMileage >= mileageFrom && carMileage <= mileageTo;
    }

    private boolean fuelTypeMatch(UserFilter filter, Map<String, String> details) {
        String carFuelType = details.get(Details.FUEL_TYPE.name);
        List<FuelType> filterFuelTypes = filter.getFuelTypes();
        if(filterFuelTypes == null || filterFuelTypes.isEmpty()) return true;
        return carFuelType != null && filterFuelTypes.stream()
                .map(FuelType::getName)
                .anyMatch(fuelType -> fuelType.equals(carFuelType));
    }

    private boolean gearboxTypeMatch(UserFilter filter, Map<String, String> details) {
        String carGearboxType = details.get(Details.GEARBOX.name);
        if (filter.getGearboxes() == null) return true;
        List<String> filterGearboxTypes = filter.getGearboxes().stream().map(GearboxType::getName).toList();
        if(filterGearboxTypes.isEmpty()) return true;
        return carGearboxType != null && filterGearboxTypes.stream()
                .anyMatch(gearboxType -> gearboxType.equals(carGearboxType));
    }

    private boolean damagedMatch(UserFilter filter, Map<String, String> details) {
        String carDamaged = details.get(Details.DAMAGED.name);
        Boolean filterDamaged = filter.getDamaged();
        if(filterDamaged == null) return true;
        return carDamaged != null && carDamaged.equals(filterDamaged ? YES_PL.getValue() : NO_PL.getValue());
    }
}
