package com.example.CryptoPortfolioTracker.repository;

import com.example.CryptoPortfolioTracker.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findByUserId(int userId);

    List<Holding> findByUserIdOrderByBoughtDateDesc(int userId);

    List<Holding> findByUserIdAndCoinSymbol(int userId, String coinSymbol);
}