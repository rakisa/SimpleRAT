package com.evil.rs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evil.rs.entity.Machine;
import com.evil.rs.vo.ConfigVo;

public interface MachineService extends IService<Machine> {

    /**
     * 根据上线机器信息返回对应的配置信息
     * @param machine 上线机器信息
     * @return 配置信息，包括休眠时长和token
     */
    ConfigVo getConfigByMachineInformation(Machine machine);
}
