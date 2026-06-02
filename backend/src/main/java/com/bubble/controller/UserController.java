package com.bubble.controller;

import com.bubble.common.result.Result;
import com.bubble.dto.UserLoginDTO;
import com.bubble.dto.UserRegisterDTO;
import com.bubble.dvo.UserVO;
import com.bubble.service.UserService;
import com.bubble.service.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @PostMapping("/send-code")
    public Result<Void> sendVerificationCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        verificationCodeService.generateAndSaveCode(email);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(HttpServletRequest request, @RequestBody UserVO userVO) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUserInfo(userId, userVO);
        return Result.success();
    }
}
