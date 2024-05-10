package com.evil.rs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.evil.rs.entity.Command;
import com.evil.rs.model.CommandModel;
import com.evil.rs.model.QueryModel;
import com.evil.rs.model.ReceiverModel;
import com.evil.rs.utils.Result;
import com.evil.rs.vo.CommandVo;

import java.util.List;

public interface CommandService extends IService<Command> {

    /**
     * 查询最后一次给目标设置的休眠时长
     * @param target 目标的token信息
     * @return 设置的休眠时长
     */
    Integer queryTargetTime(String target);

    List<CommandVo> listByToken(String token);

    void saveExecutionResult(String token, ReceiverModel receiverModel);

    String getBinaryData(String token, String id);

    Result addTask(CommandModel commandModel, String token);

    IPage<Command> listTask(QueryModel model);

    Command getCommandByTokenAndId(String token, String id);
}
