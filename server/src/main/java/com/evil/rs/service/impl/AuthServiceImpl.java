package com.evil.rs.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.evil.rs.config.ServerConfig;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ResultEnum;
import com.evil.rs.model.AuthUser;
import com.evil.rs.service.AuthService;
import com.evil.rs.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public Result authentication(AuthUser authUser) {
        boolean success = false;
        String key = ServerConfig.DEFAULT_KEY;
        File keyFile = new File("key");
        if (keyFile.exists()){
            try{
                InputStream in = new FileInputStream(CommonStringEnums.SECRET_KEY_FILE.getDescription());
                byte[] data = in.readAllBytes();
                String fileKey = new String(data);
                fileKey = fileKey.trim();
                if (fileKey.equals(authUser.getPassword())){
                    success = true;
                }
            } catch (FileNotFoundException e){
            } catch (IOException e) {}
        } else {
            if (key.equals(authUser.getPassword())) success = true;
        }
        if (success){
            StpUtil.login(authUser);
            return Result.Ok(ResultEnum.LOGIN_SUCCESS.getCode(), ResultEnum.LOGIN_SUCCESS.getMsg()).setData(StpUtil.getTokenInfo());
        }
        return Result.Ok(ResultEnum.LOGIN_FAIL.getCode(), ResultEnum.LOGIN_FAIL.getMsg());
    }
}
