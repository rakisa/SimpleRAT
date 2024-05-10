package com.evil.rs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evil.rs.entity.Command;
import com.evil.rs.entity.NotifyMessage;
import com.evil.rs.enums.CommonNumberEnums;
import com.evil.rs.enums.CommonStringEnums;
import com.evil.rs.enums.ExceptionEnums;
import com.evil.rs.exception.HandlerException;
import com.evil.rs.mapper.CommandMapper;
import com.evil.rs.model.CommandModel;
import com.evil.rs.model.QueryModel;
import com.evil.rs.model.ReceiverModel;
import com.evil.rs.service.CommandService;
import com.evil.rs.socket.WebSocketServer;
import com.evil.rs.utils.Result;
import com.evil.rs.vo.CommandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandServiceImpl extends ServiceImpl<CommandMapper, Command> implements CommandService {

    @Override
    public Integer queryTargetTime(String target) {
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Command::getTarget, target);
        queryWrapper.eq(Command::getAction, CommonNumberEnums.TASK_SLEEP.getCode());
        queryWrapper.orderByDesc(Command::getCreateTime);
        queryWrapper.last("limit 1");
        Command command = baseMapper.selectOne(queryWrapper);
        return command == null ? CommonNumberEnums.DEFAULT_SLEEP_TIME.getCode() : Integer.parseInt(command.getData());
    }

    @Override
    public List<CommandVo> listByToken(String token) {
        List<CommandVo> tasklist = new ArrayList<>();
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Command::getTarget, token);
        queryWrapper.eq(Command::getFlag, false);
        List<Command> commands = baseMapper.selectList(queryWrapper);
        for (Command command : commands) {
            CommandVo commandVo = new CommandVo();
            BeanUtils.copyProperties(command, commandVo);
            tasklist.add(commandVo);
        }
        System.out.println(tasklist);
        return tasklist;
    }

    @Override
    public void saveExecutionResult(String token, ReceiverModel receiverModel) {
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Command::getId, receiverModel.getId());
        queryWrapper.eq(Command::getTarget, token);
        Command command = baseMapper.selectOne(queryWrapper);
        if (command == null){
            throw new HandlerException(ExceptionEnums.TASK_NOT_EXIST.getCode(), ExceptionEnums.TASK_NOT_EXIST.getMsg());
        }
        command.setResult(receiverModel.getResult());
        command.setFlag(true);
        baseMapper.updateById(command);
        // 通知目标
        NotifyMessage notifyMessage = new NotifyMessage(CommonStringEnums.NOTIFY_TASK_UPDATE.getDescription(), JSON.toJSONString(command));
        WebSocketServer.notifyReceiver(command.getReceiver(), JSON.toJSONString(notifyMessage));
    }

    @Override
    public String getBinaryData(String token, String id) {
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Command::getTarget, token);
        queryWrapper.eq(Command::getId, id);
        Command command = baseMapper.selectOne(queryWrapper);
        return command != null ? command.getData() : "";
    }

    @Override
    public Result addTask(CommandModel commandModel, String token) {
        Command command = new Command();
        BeanUtils.copyProperties(commandModel, command);
        command.setReceiver(token);
        command.setFlag(false);
        if (baseMapper.insert(command) > 0) {
            return Result.Ok();
        }
        return Result.Fail();
    }

    @Override
    public IPage<Command> listTask(QueryModel model) {
        Page<Command> commandPage = new Page<>(model.getPage(), model.getSize());
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        if (model.getTarget() != null && !model.getTarget().isEmpty()) {
            queryWrapper.eq(Command::getTarget, model.getTarget());
        }
        if (model.getReceiver() != null && !model.getReceiver().isEmpty()){
            queryWrapper.eq(Command::getReceiver, model.getReceiver());
        }
        queryWrapper.orderByDesc(Command::getCreateTime);
        return baseMapper.selectPage(commandPage, queryWrapper);
    }

    @Override
    public Command getCommandByTokenAndId(String token, String id) {
        LambdaQueryWrapper<Command> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Command::getTarget, token);
        queryWrapper.eq(Command::getId, id);
        return baseMapper.selectOne(queryWrapper);
    }
}
