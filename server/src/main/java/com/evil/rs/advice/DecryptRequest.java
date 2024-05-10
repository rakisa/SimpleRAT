package com.evil.rs.advice;

import com.evil.rs.annotation.Decrypt;
import com.evil.rs.service.EncryptServices;
import com.evil.rs.utils.AESUtil;
import com.evil.rs.utils.ValidUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * 解密请求拦截
 */
@ControllerAdvice
@Slf4j
public class DecryptRequest extends RequestBodyAdviceAdapter {

    @Autowired
    EncryptServices encryptServices;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(Decrypt.class) || methodParameter.hasParameterAnnotation(Decrypt.class);
    }

    @SneakyThrows
    @Override
    public HttpInputMessage beforeBodyRead(final HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        // 获取request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 判断是否需要解密
//        if(needDecrypt(request)){
            byte[] body = new byte[request.getContentLength()];
            String remoteAddress = ValidUtil.getRealIpAddr(request);
            // 获取上一次的key
            String key = encryptServices.getAesKey(remoteAddress);
            inputMessage.getBody().read(body);
            byte[] decrypt = AESUtil.decrypt(body, key.getBytes());
            final ByteArrayInputStream bais = new ByteArrayInputStream(decrypt);
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return bais;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
//        }
//        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }
    // 对url进行判断是否需要解密请求体
    private Boolean needDecrypt(HttpServletRequest request) {
//        if(request.getRequestURI().equals("/init/info")){
//            return true;
//        }
//        String id = request.getHeader("UUID");
//        // 是否是存在下发的任务
//        CommandDo commandDo = commandService.getById(id);
//        if(id == null || id.trim().length() == 0 || commandDo == null){
//            throw new RuntimeException();
//        }
//        // 是二进制数据或初始化
//        if(commandDo.getOperation() == 206 || commandDo.getOperation() == 301 || request.getRequestURI().equals("/")){
//            return false;
//        }
        return true;
    }


}

