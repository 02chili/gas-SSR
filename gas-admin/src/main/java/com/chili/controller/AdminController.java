package com.chili.controller;

import com.chili.dto.AdminLoginDTO;
import com.chili.entity.Admin;
import com.chili.service.AdminService;
import com.chili.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chili.result.Result;
import com.chili.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/login")
    public Result login(@RequestBody AdminLoginDTO adminLoginDTO){
        log.info("员工登录");
        Admin admin = adminService.login(adminLoginDTO);
        //生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("username", admin.getUsername());
        String token = JwtUtil.createJWT(
                "02chili",
                7200000,
                claims);
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        BeanUtils.copyProperties(admin, adminLoginVO);
        adminLoginVO.setToken(token);
        return Result.success(adminLoginVO);
    }
    @PostMapping("/enroll")
    public Result enroll(@Validated @RequestBody AdminLoginDTO mannerDTO) {
        // 后台管理人员注册
        try {
            log.debug("管理员注册");
            adminService.enroll(mannerDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("管理员注册失败: {}", e.getMessage(), e);
            return Result.error("注册失败，请稍后再试");
        }
    }

}
