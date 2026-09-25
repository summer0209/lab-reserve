package com.labreserve;

import com.labreserve.room.Room;
import com.labreserve.room.RoomRepository;
import com.labreserve.user.User;
import com.labreserve.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public DataInitializer(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }
    @Override
    public void run(String... args) {
        if (!userRepository.existsByStudentNo("admin")) {
            User admin = new User();
            admin.setStudentNo("admin");
            admin.setName("管理员");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
        if (!userRepository.existsByStudentNo("2024001")) {
            User student = new User();
            student.setStudentNo("2024001");
            student.setName("张三");
            student.setPassword("123456");
            student.setRole("STUDENT");
            userRepository.save(student);
        }
        if (roomRepository.count() == 0) {
            roomRepository.save(room("A101", "教学楼A", 40, "投影仪,白板"));
            roomRepository.save(room("B203", "实验楼B", 24, "电脑,实验台"));
            roomRepository.save(room("C301", "图书馆C", 16, "投影仪"));
        }
    }

    private Room room(String name, String building, int capacity, String equipment) {
        Room room = new Room();
        room.setName(name);
        room.setBuilding(building);
        room.setCapacity(capacity);
        room.setEquipment(equipment);
        room.setStatus("ACTIVE");
        return room;
    }
}