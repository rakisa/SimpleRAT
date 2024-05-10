package com.evil.rs.controller;

import com.evil.rs.model.AuthUser;
import com.evil.rs.service.AuthService;
import com.evil.rs.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    /**
     * 根据 用户名和密码进行认证
     * @param authUser 认证用户对象
     * @return token信息
     */
    @PostMapping("/doAuth")
    public Result doAuth(@RequestBody AuthUser authUser) {
        return authService.authentication(authUser);
    }

}
