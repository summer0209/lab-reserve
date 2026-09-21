package com.labreserve.room;

import com.labreserve.common.BizException;
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
        if (roomRepository.existsByBuildingAndName(room.getBuilding(), room.getName())) {
            throw new BizException("同一栋楼已存在同名教室");
        }
        return roomRepository.save(room);
    }

    public List<Room> list() {
        return roomRepository.findAll();
    }

    public Room getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BizException("教室不存在：" + id));
    }

    public Room update(Long id, Room incoming) {
        Room room = getById(id);
        if (roomRepository.existsByBuildingAndNameAndIdNot(
                incoming.getBuilding(), incoming.getName(), id)) {
            throw new BizException("同一栋楼已存在同名教室");
        }
        room.setName(incoming.getName());
        room.setBuilding(incoming.getBuilding());
        room.setCapacity(incoming.getCapacity());
        room.setEquipment(incoming.getEquipment());
        if (incoming.getStatus() != null && !incoming.getStatus().isBlank()) {
            room.setStatus(incoming.getStatus());
        }
        return roomRepository.save(room);
    }

    public void delete(Long id) {
        Room room = getById(id);
        room.setStatus("INACTIVE");
        roomRepository.save(room);
    }
}