package com.cbw.security.jwt.admin.service;

import com.cbw.security.jwt.admin.dto.RequestAdmin;
import com.cbw.security.jwt.admin.dto.ResponseAdmin;

public interface AdminService {
    ResponseAdmin.Info signup(RequestAdmin.Register registerDto);
}
