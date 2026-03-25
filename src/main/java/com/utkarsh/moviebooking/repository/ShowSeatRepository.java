package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.ShowSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    List<ShowSeat> findByShowId(Long showId);

    // Used to lock specific seats during booking
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.id IN :ids")
    List<ShowSeat> findByIdsWithLock(@Param("ids") List<Long> ids);
}
