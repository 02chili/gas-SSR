package com.chili.service;

import com.chili.dto.AdminLoginDTO;
import com.chili.entity.Admin;

public interface AdminService {
    Admin login(AdminLoginDTO adminLoginDTO);

    void enroll(AdminLoginDTO mannerDTO);
}
