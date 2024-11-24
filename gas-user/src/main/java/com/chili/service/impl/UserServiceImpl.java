package com.chili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chili.dto.UserEnrollDTO;
import com.chili.dto.UserLoginDTO;
import com.chili.entity.User;
import com.chili.exception.AccountNotExistException;
import com.chili.exception.PasswordErrorException;
import com.chili.mapper.UserMapper;
import com.chili.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userLoginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) throw new AccountNotExistException("账号不存在");
        if (!user.getPassword().equals(userLoginDTO.getPassword())) {
            throw new PasswordErrorException("密码错误");
        }
        return user;
    }

    @Override
    public void enroll(UserEnrollDTO userEnrollDTO) {

    }
}
