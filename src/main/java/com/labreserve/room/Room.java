package com.labreserve.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 32)
    private String name;
    @Column(nullable = false, length = 64)
    private String building;
    @Column(nullable = false)
    private Integer capacity;
    @Column(length = 128)
    private String equipment;
    @Column(nullable = false, length = 16)
    private String status = "ACTIVE";
}
