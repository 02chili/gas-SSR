package com.chili.service;

import com.chili.dto.UserEnrollDTO;
import com.chili.dto.UserLoginDTO;
import com.chili.entity.User;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);

    void enroll(UserEnrollDTO userEnrollDTO);
}
