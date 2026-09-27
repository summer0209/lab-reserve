package com.labreserve.user;

import com.labreserve.common.BizException;
import com.labreserve.reservation.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ReservationRepository reservationRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String studentNo, String name, String password) {
        if (userRepository.existsByStudentNo(studentNo)) {
            throw new BizException("学号已注册");
        }
        User user = new User();
        user.setStudentNo(studentNo);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("STUDENT");
        return userRepository.save(user);
    }

    public User login(String studentNo, String password) {
        User user = userRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new BizException("学号或密码错误"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException("学号或密码错误");
        }
        return user;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException("用户不存在：" + id));
    }

    @Transactional
    public void deleteMe(Long userId) {
        User user = getById(userId);
        if ("ADMIN".equals(user.getRole())) {
            throw new BizException("管理员账号不能注销");
        }
        reservationRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }
}
