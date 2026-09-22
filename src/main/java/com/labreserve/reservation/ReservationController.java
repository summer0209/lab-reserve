package com.labreserve.reservation;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation create(@RequestBody Map<String, String> body) {
        return reservationService.create(
                Long.valueOf(body.get("userId")),
                Long.valueOf(body.get("roomId")),
                LocalDate.parse(body.get("reserveDate")),
                LocalTime.parse(body.get("startTime")),
                LocalTime.parse(body.get("endTime")),
                body.get("purpose")
        );
    }

    @GetMapping("/me")
    public List<Reservation> mine(@RequestParam Long userId) {
        return reservationService.listMine(userId);
    }

    @PostMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id, @RequestParam Long userId) {
        return reservationService.cancel(userId, id);
    }
}
