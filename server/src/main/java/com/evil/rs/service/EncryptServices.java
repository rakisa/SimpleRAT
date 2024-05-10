package com.evil.rs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evil.rs.entity.EncryptKeys;

import javax.servlet.http.HttpServletRequest;

public interface EncryptServices extends IService<EncryptKeys> {
    String genRandomAesKey(HttpServletRequest request);

    String getAesKey(String ipAddr);
}
