package com.chili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chili.dto.AdminLoginDTO;
import com.chili.entity.Admin;
import com.chili.exception.AccountNotExistException;
import com.chili.mapper.AdminMapper;
import com.chili.service.AdminService;
import com.chili.exception.PasswordErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    @Override
    public Admin login(AdminLoginDTO adminLoginDTO){
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", adminLoginDTO.getUsername());
        List<Admin> admins = adminMapper.selectList(queryWrapper);
        if (admins.size() == 0) throw new AccountNotExistException("账户不存在");
        Admin admin = admins.get(0);
        if (!admin.getPassword().equals(adminLoginDTO.getPassword())) throw new PasswordErrorException("密码错误");
        return admin;
    }

    @Override
    public void enroll(AdminLoginDTO mannerDTO) {
        Admin admin = new Admin();
        admin.setUsername(mannerDTO.getUsername());
        admin.setPassword(mannerDTO.getPassword());
        adminMapper.insert(admin);
    }
}
