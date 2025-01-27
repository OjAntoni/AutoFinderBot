package com.example.autofinderbot.repository;

import com.example.autofinderbot.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
