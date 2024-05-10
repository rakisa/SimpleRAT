use super::common::config::Config;

pub mod network;
pub mod aes_util;
pub mod sys_util;
pub mod fs_util;
pub mod device_util;

pub struct FSUtil;

pub struct AESUtil;

pub struct NetWork{
    pub config: Config
}
pub struct SysUtil;
pub struct DeviceUtil;