package com.evil.rs.utils;

import com.evil.rs.config.ConstConfig;

import javax.servlet.http.HttpServletRequest;

public class ValidUtil {

    public static String getRealIpAddr(HttpServletRequest request){
        String routerHeader = request.getHeader(ConstConfig.routerHeader);
        if (routerHeader != null){
            // 请求上携带了转发的校验头
            if (routerHeader.equals(ConstConfig.routerValue)){
                return request.getHeader(ConstConfig.realIpHeader);
            }
        }
        return request.getRemoteAddr();
    }

}
