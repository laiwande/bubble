package com.bubble.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.dto.UserLoginDTO;
import com.bubble.dto.UserRegisterDTO;
import com.bubble.dvo.UserVO;
import com.bubble.entity.User;

public interface UserService extends IService<User> {
    String login(UserLoginDTO loginDTO);
    void register(UserRegisterDTO registerDTO);
    UserVO getUserInfo(Long userId);
    void updateUserInfo(Long userId, UserVO userVO);
}
