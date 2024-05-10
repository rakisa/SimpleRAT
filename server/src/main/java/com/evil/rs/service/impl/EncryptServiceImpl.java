package com.evil.rs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evil.rs.entity.EncryptKeys;
import com.evil.rs.enums.ExceptionEnums;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.mapper.EncryptKeysMapper;
import com.evil.rs.service.EncryptServices;
import com.evil.rs.utils.AESUtil;
import com.evil.rs.utils.ValidUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class EncryptServiceImpl extends ServiceImpl<EncryptKeysMapper, EncryptKeys> implements EncryptServices {
    @Override
    public String genRandomAesKey(HttpServletRequest request) {
        String realIp = ValidUtil.getRealIpAddr(request);
        QueryWrapper<EncryptKeys> queryWrapper = new QueryWrapper<EncryptKeys>();
        queryWrapper.eq("ip", realIp);
        EncryptKeys encryptKeys = baseMapper.selectOne(queryWrapper);
        if (encryptKeys != null){
            return encryptKeys.getAesKey();
        }
        String key = AESUtil.generateRandomKey(realIp);
        baseMapper.insert(new EncryptKeys(realIp, key));
        return key;
    }

    /**
     * 从数据库查找对应的aes加密密钥
     * @param ipAddr 请求Ip地址
     * @return aes密钥
     */
    @Override
    public String getAesKey(String ipAddr) {
        QueryWrapper<EncryptKeys> queryWrapper = new QueryWrapper<EncryptKeys>();
        queryWrapper.eq("ip", ipAddr);
        EncryptKeys encryptKeys = baseMapper.selectOne(queryWrapper);
        if (encryptKeys != null){
            return encryptKeys.getAesKey();
        }
        throw new HandlerException(ExceptionEnums.AES_KEY_NOT_FOUND.getCode(), ExceptionEnums.AES_KEY_NOT_FOUND.getMsg());
    }
}
