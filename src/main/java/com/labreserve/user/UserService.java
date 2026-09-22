package com.labreserve.user;

import com.labreserve.common.BizException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User register(String studentNo, String name, String password){
        if (userRepository.existsByStudentNo(studentNo)) {
            throw new BizException("学号已注册");
        }
        User user = new User();
        user.setStudentNo(studentNo);
        user.setName(name);
        user.setPassword(password);
        user.setRole("STUDENT");
        return userRepository.save(user);
    }
    public User login(String studentNo, String password) {
        User user = userRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new BizException("学号或密码错误"));
        if (!user.getPassword().equals(password)) {
            throw new BizException("学号或密码错误");
        }
        return user;
    }
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException("用户不存在：" + id));
    }
}

