package com.utkarsh.moviebooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long HOLD_DURATION_MINUTES = 10;

    // Key format: "seat_hold:{showId}:{seatId}"
    private String buildKey(Long showId, Long seatId) {
        return "seat_hold:" + showId + ":" + seatId;
    }

    // Returns false if the seat is already held by someone else
    public boolean holdSeat(Long showId, Long seatId, Long userId) {
        String key = buildKey(showId, seatId);

        // setIfAbsent = SET key value NX EX — atomic operation
        // If key already exists (someone else holds it), returns false
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                key,
                userId.toString(),
                Duration.ofMinutes(HOLD_DURATION_MINUTES)
        );

        return Boolean.TRUE.equals(success);
    }

    public void releaseHold(Long showId, Long seatId) {
        redisTemplate.delete(buildKey(showId, seatId));
    }

    public boolean isHeldByUser(Long showId, Long seatId, Long userId) {
        Object value = redisTemplate.opsForValue().get(buildKey(showId, seatId));
        return userId.toString().equals(value);
    }
}
