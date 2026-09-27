package com.labreserve.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        User user = userService.register(
                body.get("studentNo"),
                body.get("name"),
                body.get("password")
        );
        return toPublic(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        User user = userService.login(body.get("studentNo"), body.get("password"));
        CurrentUser.save(session, user);
        return toPublic(user);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "已退出");
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        Long id = CurrentUser.requireId(session);
        User user = userService.getById(id);
        return toPublic(user);
    }

    @DeleteMapping("/me")
    public Map<String, String> deleteMe(HttpSession session) {
        Long id = CurrentUser.requireId(session);
        userService.deleteMe(id);
        session.invalidate();
        return Map.of("message", "账号已注销");
    }

    private Map<String, Object> toPublic(User user) {
        return Map.of(
                "id", user.getId(),
                "studentNo", user.getStudentNo(),
                "name", user.getName(),
                "role", user.getRole()
        );
    }
}
