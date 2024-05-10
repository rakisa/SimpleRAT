package com.evil.rs.filter;

import com.alibaba.fastjson.JSON;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ExceptionEnums;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.service.EncryptServices;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.utils.AESUtil;
import com.evil.rs.utils.ValidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 过滤器将加密的请求url进行解密还原
 */
@Slf4j
public class UrlFilter implements Filter {

    @Autowired
    EncryptServices encryptServices;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取原始ip地址、访问url等信息
        String ipAddress = ValidUtil.getRealIpAddr(request);
        String rawUrl = request.getRequestURI();
        log.info("rawUrl:" + rawUrl);
        final String SLASH = "/";
        String encryptUrl = rawUrl.replaceFirst(SLASH, "");
        // 过滤客户端发送的请求
        if(checkUrlControllerApi(rawUrl)){
            byte[] decrypt;
            try {
                String aesKey = encryptServices.getAesKey(ipAddress);
                decrypt = AESUtil.decrypt(encryptUrl.getBytes(StandardCharsets.UTF_8), aesKey.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.warn("[-]ip: {}访问url: {}时解密失败,错误信息: {}", ipAddress, encryptUrl, e.getMessage());
                return;
            }
            String decryptUrl = new String(decrypt);
            decryptUrl = SLASH + decryptUrl;
            log.info("[+]ip: {}访问加密url: {}", ipAddress, decryptUrl);
            // 下发机器心跳信息
            WebSocketServer.notifyAllUser(
                    JSON.toJSONString(
                            new NotifyMessage(CommonStringEnums.NOTIFY_HEARTBEAT_MSG.getDescription(), request.getHeader("token"))
                    )
            );
            request.getRequestDispatcher(decryptUrl).forward(request, response);
            return;
        }
        // 控制端请求接口
        log.info("[+]ip: {}访问了url: {}", ipAddress, rawUrl);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean checkUrlControllerApi(String rawUrl){
        return !"/".equals(rawUrl) && !rawUrl.equals("/ping") && !rawUrl.startsWith("/command") && !rawUrl.startsWith("/auth") && !rawUrl.startsWith("/controller") && !rawUrl.startsWith("/chat");
    }
}
