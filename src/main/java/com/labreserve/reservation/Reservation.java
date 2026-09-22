package com.labreserve.reservation;

import com.labreserve.room.Room;
import com.labreserve.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private LocalDate reserveDate;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(length = 128)
    private String purpose;
    @Column(nullable = false, length = 16)
    private String status = "APPROVED";
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
