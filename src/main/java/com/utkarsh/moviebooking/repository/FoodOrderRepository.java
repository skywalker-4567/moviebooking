package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByBookingId(Long bookingId);
}
