use serde::{Deserialize, Serialize};
/**
 * 通信配置
 * @field token 机器token
 * @field time beacon休眠时间
 * @field key aes密钥
 */
#[derive(Serialize, Deserialize, Debug)]
pub struct ChannelConfig {

    pub token: String,
    pub time: u16,
    pub key: String

}

/**
 * 机器上线信息
 * @field os 操作系统
 * @field arch 操作系统架构
 * @field kernel_version 内核版本
 * @field host_name 主机名
 * @field mac_address mac地址
 * @field cwd 程序当前运行目录
 */
#[derive(Serialize, Debug)]
pub struct MachineInformation {

    pub os: String,
    pub arch: String,
    pub kernel_version: String,
    pub host_name: String,
    pub mac_address: String,
    pub cwd: String

}

/**
 * http交互响应格式
 * code 状态码
 * msg 消息
 * data 数据
 */
#[derive(Deserialize, Debug)]
pub struct HttpResult {

    pub code: u8,
    pub msg: String,
    pub data: String

}

/**
 * 任务队列
 * @ field id 任务id
 * @ field data 任务的描述信息
 * @ field action 任务类型
 */
#[derive(Deserialize, Debug)]
pub struct Task {

    pub id: String,
    pub data: String,
    pub action: u16

}


/**
 * 执行完毕后返回的结果
 * @ field 任务id
 * @ field 任务执行结果
 */
#[derive(Serialize, Debug)]
pub struct ReceiverTask {
    pub id: String,
    pub result: String
}

#[derive(Serialize, Debug)]
pub struct DirectorInfo {
    pub cwd: String,
    pub files: Files
}

/**
 * 文件列表属性
 */
#[derive(Serialize, Debug)]
pub struct FileMeta {
    pub name: String,
    pub is_file: bool,
    pub size: u64
}

/**
 * socket交互模型
 */
#[derive(Serialize, Debug)]
pub struct SocketMessage{
    pub route: String,
    pub data: String,
    pub token: String
}

impl SocketMessage {
    pub fn new(route: String, data: String, token: String) -> Self{
        SocketMessage{
            route,
            data,
            token
        }
    }
}

pub type Tasks = Vec<Task>;
pub type Files = Vec<FileMeta>;