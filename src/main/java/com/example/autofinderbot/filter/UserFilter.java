package com.example.autofinderbot.filter;

import com.example.autofinderbot.domain.*;
import com.example.autofinderbot.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import static com.example.autofinderbot.domain.Seller.SellerType.fromSearchKey;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;

@Entity
@Data
public class UserFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    String searchUrl;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "user_filter_2_car_brands",
            joinColumns = @JoinColumn(name = "user_filter_id"),
            inverseJoinColumns = @JoinColumn(name = "car_brand_id")
    )
    private List<CarBrand> carBrands;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "user_filter_2_car_models",
            joinColumns = @JoinColumn(name = "user_filter_id"),
            inverseJoinColumns = @JoinColumn(name = "car_model_id")
    )
    private List<CarModel> carModels;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "user_filter_2_generations",
            joinColumns = @JoinColumn(name = "user_filter_id"),
            inverseJoinColumns = @JoinColumn(name = "generation_id")
    )
    private List<Generation> generations;

    @Column(name = "price_start")
    private Long priceStart;

    @Column(name = "price_end")
    private Long priceEnd;

    @Column(name = "year_from")
    private Integer yearFrom;

    @Column(name = "year_to")
    private Integer yearTo;

    @Column(name = "mileage_from")
    private Integer mileageFrom;

    @Column(name = "mileage_to")
    private Integer mileageTo;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "user_filter_2_fuel_types",
            joinColumns = @JoinColumn(name = "user_filter_id"),
            inverseJoinColumns = @JoinColumn(name = "fuel_type_id")
    )
    @Column(name = "fuel_type")
    private List<FuelType> fuelTypes;

    @Column(name = "gearbox_type")
    @ElementCollection(fetch = EAGER)
    @Enumerated(STRING)
    @CollectionTable(name = "user_filter_2_gearbox", joinColumns = @JoinColumn(name = "user_filter_id"))
    private List<GearboxType> gearboxes;

    private Boolean damaged;

    private String sellerType;

    private boolean confirmed;

    private boolean active;

    @Enumerated(STRING)
    private State state;

    public enum State {
        NEW,
        OLD
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\uD83D\uDCA1 *Status*: ");
        sb.append(active ? "Active ✅" : "Stopped ❌");
        sb.append("\n");

        sb.append(filterParametersOnly());

        return sb.toString();
    }

    public String filterParametersOnly() {
        StringBuilder sb = new StringBuilder();

        sb.append("🚗 *Car Brands*: ");
        if (carBrands != null && !carBrands.isEmpty()) {
            sb.append(String.join(", ", carBrands.stream().map(CarBrand::getName).toList()));
        } else {
            sb.append("All");
        }
        sb.append("\n");

        sb.append("🚘 *Car Models*: ");
        if (carModels != null && !carModels.isEmpty()) {
            sb.append(String.join(", ", carModels.stream().map(CarModel::getName).toList()));
        } else {
            sb.append("All");
        }
        sb.append("\n");

        sb.append("📖 *Generations*: ");
        if (generations != null && !generations.isEmpty()) {
            sb.append(String.join(", ", generations.stream().map(Generation::getName).toList()));
        } else {
            sb.append("All");
        }
        sb.append("\n");

        sb.append("💰 *Price Range*: ")
                .append(priceStart != null ? priceStart : "N/A")
                .append(" - ")
                .append(priceEnd != null ? priceEnd : "N/A")
                .append("\n");

        sb.append("📅 *Year Range*: ")
                .append(yearFrom != null ? yearFrom : "N/A")
                .append(" - ")
                .append(yearTo != null ? yearTo : "N/A")
                .append("\n");

        sb.append("🛣️ *Mileage Range*: ")
                .append(mileageFrom != null ? mileageFrom + " km" : "N/A")
                .append(" - ")
                .append(mileageTo != null ? mileageTo + " km" : "N/A")
                .append("\n");

        sb.append("⛽ *Fuel Types*: ");
        if (fuelTypes != null && !fuelTypes.isEmpty()) {
            sb.append(String.join(", ", fuelTypes.stream().map(FuelType::getName).toList()));
        } else {
            sb.append("All");
        }
        sb.append("\n");

        sb.append("🚦 *Gearbox*: ");
        if (gearboxes != null && !gearboxes.isEmpty()) {
            sb.append(String.join(", ", gearboxes.stream().map(GearboxType::getName).toList()));
        } else {
            sb.append("All");
        }
        sb.append("\n");

        sb.append(" \uD83D\uDEA7 *Damaged*: ");
        sb.append(damaged != null ? (damaged ? "Yes" : "No") : "N/A");
        sb.append("\n");

        sb.append(" \uD83D\uDCBC *Seller Type*: ");
        sb.append(sellerType != null ? fromSearchKey(sellerType) : "N/A");
        sb.append("\n");

        return sb.toString();
    }
}
