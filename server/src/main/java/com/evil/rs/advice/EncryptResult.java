package com.evil.rs.advice;

import com.evil.rs.annotation.Encrypt;
import com.evil.rs.config.ConstConfig;
import com.evil.rs.service.EncryptServices;
import com.evil.rs.utils.AESUtil;
import com.evil.rs.utils.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 响应处理
 */
@ControllerAdvice
public class EncryptResult implements ResponseBodyAdvice<Result> {

    @Autowired
    EncryptServices encryptServices;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Encrypt.class);
    }

    @Override
    public Result beforeBodyWrite(Result body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String ipAddress = getRealIpAddr(request);
        System.out.println("解密返回包ip:" + ipAddress);
        String aesKey = encryptServices.getAesKey(ipAddress);
        try {
            if (body.getData() != null) {
                body.setData(AESUtil.encrypt(objectMapper.writeValueAsBytes(body.getData()), aesKey.getBytes()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    private String getRealIpAddr(ServerHttpRequest request){
        String routerHeader = request.getHeaders().getFirst(ConstConfig.routerHeader);;
        if (routerHeader != null){
            // 请求上携带了转发的校验头
            if (routerHeader.equals(ConstConfig.routerValue)){
                return request.getHeaders().getFirst(ConstConfig.realIpHeader);
            }
        }
        return request.getRemoteAddress().getAddress().toString().replaceFirst("/", "");
    }

}
