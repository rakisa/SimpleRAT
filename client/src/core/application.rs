use std::error::Error;

pub struct Application;

use crate::core::{ channel::init_channel, common::config::Config, util::NetWork};

use super::util::SysUtil;

impl Application{
    pub fn run() -> Result<(), Box<dyn Error>> {
        // 创建计划任务
        let _ = SysUtil::create_task();
        // 初始化配置
        let config = Config::init();
        let network = NetWork::init(config);
        let connect_type = network.auto_switch_connect();
        // 初始化通信管道
        let mut adapter_channel = init_channel(connect_type, network)?;
        adapter_channel.start()?;
        adapter_channel.work();
        Ok(())
    }

    
}