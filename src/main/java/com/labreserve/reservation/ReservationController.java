package com.labreserve.reservation;

import com.labreserve.user.CurrentUser;
import jakarta.servlet.http.HttpSession;
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
    public Reservation create(@RequestBody Map<String, String> body, HttpSession session) {
        return reservationService.create(
                CurrentUser.requireId(session),
                Long.valueOf(body.get("roomId")),
                LocalDate.parse(body.get("reserveDate")),
                LocalTime.parse(body.get("startTime")),
                LocalTime.parse(body.get("endTime")),
                body.get("purpose")
        );
    }

    @GetMapping("/me")
    public List<Reservation> mine(HttpSession session) {
        return reservationService.listMine(CurrentUser.requireId(session));
    }

    @GetMapping("/pending")
    public List<Reservation> pending(HttpSession session) {
        CurrentUser.requireAdmin(session);
        return reservationService.listPending();
    }

    @PostMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id, HttpSession session) {
        return reservationService.cancel(CurrentUser.requireId(session), id);
    }

    @PostMapping("/{id}/approve")
    public Reservation approve(@PathVariable Long id, HttpSession session) {
        return reservationService.approve(CurrentUser.requireId(session), id);
    }

    @PostMapping("/{id}/reject")
    public Reservation reject(@PathVariable Long id, HttpSession session) {
        return reservationService.reject(CurrentUser.requireId(session), id);
    }
}
