package com.labreserve.room;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;


public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByBuildingAndName(String building, String name);

    boolean existsByBuildingAndNameAndIdNot(String building, String name, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}
