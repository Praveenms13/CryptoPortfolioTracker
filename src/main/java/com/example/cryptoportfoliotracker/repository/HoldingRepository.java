package com.example.CryptoPortfolioTracker.repository;

import com.example.CryptoPortfolioTracker.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findByUserId(Long userId);

    List<Holding> findByUserIdOrderByBoughtDateDesc(Long userId);

    List<Holding> findByUserIdAndCoinSymbol(Long userId, String coinSymbol);
}