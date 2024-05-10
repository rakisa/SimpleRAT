package com.evil.rs.controller;

import com.evil.rs.service.EncryptServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class InitController {

    @Autowired
    EncryptServices encryptServices;

    @PostMapping("/")
    public String genRandomAesKey(HttpServletRequest request) {
        return encryptServices.genRandomAesKey(request);
    }

}
