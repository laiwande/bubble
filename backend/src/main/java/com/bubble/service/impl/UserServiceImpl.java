package com.bubble.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.UserLoginDTO;
import com.bubble.dto.UserRegisterDTO;
import com.bubble.dvo.UserVO;
import com.bubble.entity.User;
import com.bubble.mapper.UserMapper;
import com.bubble.service.UserService;
import com.bubble.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return JwtUtil.generateToken(user.getId());
    }

    @Override
    public void register(UserRegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (getOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        save(user);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = getById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public void updateUserInfo(Long userId, UserVO userVO) {
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(userVO, user);
        updateById(user);
    }
}
