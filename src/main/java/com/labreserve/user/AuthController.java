package com.labreserve.user;

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
        return Map.of(
                "id", user.getId(),
                "studentNo", user.getStudentNo(),
                "name", user.getName(),
                "role", user.getRole()
        );
    }
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        User user = userService.login(body.get("studentNo"), body.get("password"));
        return Map.of(
                "id", user.getId(),
                "studentNo", user.getStudentNo(),
                "name", user.getName(),
                "role", user.getRole()
        );
    }
    @DeleteMapping("/me")
    public Map<String, String> deleteMe(@RequestParam Long userId) {
        userService.deleteMe(userId);
        return Map.of("message", "账号已注销");
    }
}



