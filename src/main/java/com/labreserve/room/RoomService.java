package com.labreserve.room;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room create(Room room) {
        room.setId(null);
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus("ACTIVE");
        }
        return roomRepository.save(room);
    }

    public List<Room> list() {
        return roomRepository.findAll();
    }
    public  Room getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("教室不存在：" + id)) ;
    }
}
