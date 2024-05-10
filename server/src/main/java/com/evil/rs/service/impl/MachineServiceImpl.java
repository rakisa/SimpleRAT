package com.evil.rs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evil.rs.entity.Machine;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ExceptionEnums;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.mapper.MachineMapper;
import com.evil.rs.service.CommandService;
import com.evil.rs.service.MachineService;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.vo.ConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MachineServiceImpl extends ServiceImpl<MachineMapper, Machine> implements MachineService {

    @Autowired
    CommandService commandService;

    @Override
    public ConfigVo getConfigByMachineInformation(Machine machine) {
        ConfigVo configVo = new ConfigVo();
        QueryWrapper<Machine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mac_address", machine.getMacAddress());
        Machine DBMachine = baseMapper.selectOne(queryWrapper);
        if (DBMachine == null){
            String token = UUID.randomUUID().toString();
            machine.setFlag(true);
            machine.setCurrentProtocol(machine.getCurrentProtocol());
            machine.setToken(token);
            if (baseMapper.insert(machine) == 1){
                configVo.setToken(token);
                configVo.setTime(60);
                return configVo;
            } else {
                throw new HandlerException(ExceptionEnums.MACHINE_SAVE_FAIL.getCode(), ExceptionEnums.MACHINE_SAVE_FAIL.getMsg());
            }
        }
        DBMachine.setCurrentProtocol(machine.getCurrentProtocol());
        baseMapper.updateById(DBMachine);
        configVo.setToken(DBMachine.getToken());
        configVo.setTime(commandService.queryTargetTime(DBMachine.getToken()));
        // 发送通知到控制端
        WebSocketServer.notifyAllUser(
            JSON.toJSONString(
                new NotifyMessage(CommonStringEnums.NOTIFY_ONLINE_MSG.getDescription(), "")
            )
        );
        return configVo;
    }
}
