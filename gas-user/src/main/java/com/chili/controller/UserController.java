package com.chili.controller;

import com.chili.dto.UserEnrollDTO;
import com.chili.dto.UserLoginDTO;
import com.chili.entity.User;
import com.chili.result.Result;
import com.chili.service.UserService;
import com.chili.utils.JwtUtil;
import com.chili.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.login(userLoginDTO);
        //发放jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.createJWT(
                "02chili",
                7200000,
                claims);
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        userLoginVO.setToken(token);
        return Result.success(userLoginVO);
    }
    @PostMapping("/enroll")
    public Result enroll(@RequestBody UserEnrollDTO userEnrollDTO) {
        userService.enroll(userEnrollDTO);
        return Result.success();
    }
}
