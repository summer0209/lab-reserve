package com.labreserve.room;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByBuildingAndName(String building, String name);

    boolean existsByBuildingAndNameAndIdNot(String building, String name, Long id);
}