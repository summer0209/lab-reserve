package com.labreserve.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByReserveDateDescStartTimeDesc(Long userId);

    List<Reservation> findByUserIdAndStatusNotOrderByReserveDateDescStartTimeDesc(
            Long userId, String status);

    List<Reservation> findByRoomIdAndReserveDateAndStatusNot(
            Long roomId, LocalDate reserveDate, String status);

    @Query("""
            select count(r) from Reservation r
            where r.room.id = :roomId
              and r.reserveDate = :reserveDate
              and r.status <> 'CANCELLED'
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    long countOverlap(@Param("roomId") Long roomId,
                      @Param("reserveDate") LocalDate reserveDate,
                      @Param("startTime") LocalTime startTime,
                      @Param("endTime") LocalTime endTime);
}


