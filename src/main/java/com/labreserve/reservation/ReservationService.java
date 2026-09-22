package com.labreserve.reservation;

import com.labreserve.common.BizException;
import com.labreserve.room.Room;
import com.labreserve.room.RoomService;
import com.labreserve.user.User;
import com.labreserve.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomService roomService;
    private final UserService userService;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomService roomService,
                              UserService userService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.userService = userService;
    }

    @Transactional
    public Reservation create(Long userId, Long roomId, LocalDate date,
                              LocalTime start, LocalTime end, String purpose) {
        if (!end.isAfter(start)) {
            throw new BizException("结束时间必须晚于开始时间");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new BizException("不能预约过去的时段");
        }
        if (date.isEqual(LocalDate.now()) && !start.isAfter(LocalTime.now())) {
            throw new BizException("不能预约过去的时段");
        }
        Room room = roomService.getById(roomId);
        if (!"ACTIVE".equals(room.getStatus())) {
            throw new BizException("教室已下架，无法预约");
        }
        User user = userService.getById(userId);

        long overlap = reservationRepository.countOverlap(roomId, date, start, end);
        if (overlap > 0) {
            throw new BizException("该时段已被预约");
        }

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setUser(user);
        reservation.setReserveDate(date);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setPurpose(purpose);
        reservation.setStatus("APPROVED");
        return reservationRepository.save(reservation);
    }

    public List<Reservation> listMine(Long userId) {
        return reservationRepository
                .findByUserIdAndStatusNotOrderByReserveDateDescStartTimeDesc(userId, "CANCELLED");
    }

    public Reservation cancel(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BizException("预约不存在：" + reservationId));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BizException("只能取消自己的预约");
        }
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new BizException("预约已取消");
        }
        reservation.setStatus("CANCELLED");
        return reservationRepository.save(reservation);
    }
}

