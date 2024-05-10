use std::{error::Error, fs::{self, remove_dir_all, remove_file, rename, ReadDir}, path::{Path, PathBuf}};

use serde_json::Value;

use crate::core::{common::{FAIL, SUCCESS}, model::{DirectorInfo, FileMeta}};

use super::FSUtil;

impl FSUtil {

    /**
     * @ description 列出所有可用磁盘
     */
    pub fn list_disk() -> Result<String, Box<dyn Error>> {
        #[cfg(windows)]{
            Ok(serde_json::to_string(&disk_list::get_disk_list())?)
        }
        #[cfg(not(windows))]{
            Ok(String::from("Not Support"))
        }
    }

    /**
     * @ description 列出某个目录下的文件信息
     * @ param dir 目录的路径
     */
    pub fn dir(dir: &str) -> Result<String, Box<dyn Error>> {
        let mut files = Vec::new();
        let read_dir: ReadDir = std::fs::read_dir(dir)?;
        let cwd: PathBuf = fs::canonicalize(&dir)?;
        for ele in read_dir {
            let ele = ele?;
            match ele.metadata() {
                Ok(meta) => {
                    let file = FileMeta{ 
                        name: ele.file_name().into_string().unwrap_or(String::from("unknown")), 
                        is_file: meta.is_file(), 
                        size: meta.len() 
                    };
                    files.push(file);
                },
                Err(_) => continue,
            }
        }
        let director_info = DirectorInfo { 
            cwd: cwd.to_str().unwrap_or("Unknown").to_string(), 
            files 
        };
        Ok(serde_json::to_string(&director_info)?)
    }

    /**
     * @ description 创建目录
     * @ param path 目录的路径
     */
    pub fn create_dir(path: &str) -> Result<String, Box<dyn Error>> {
        fs::create_dir_all(path)?;
        Ok(SUCCESS.to_owned())
    }

    /**
     * @ description 删除文件/目录
     * @ param path 目标路径
     */
    pub fn remove(path: &str) -> Result<String, Box<dyn Error>> {
        let path = Path::new(path);
        if path.is_dir(){
            remove_dir_all(path)?;
        }else{
            remove_file(path)?;
        }
        Ok(SUCCESS.to_owned())
    }

    /**
     * @ description 重命名
     * @ param data json字符串
     */
    pub fn rename(data: &str) -> Result<String, Box<dyn Error>> {
        let json_value: Value = serde_json::from_str(data)?;
        let binding = json_value["old"].as_str().unwrap_or("");
        let old = Path::new(&binding);
        let binding = json_value["new"].as_str().unwrap_or("");
        let new = Path::new(&binding);
        if old.exists() {
            rename(old, new)?;
            Ok(SUCCESS.to_owned())
        } else {
            Ok(FAIL.to_owned())
        }
    }

    pub fn upload(data: &str) -> Result<Vec<u8>, Box<dyn Error>> {
        Ok(fs::read(data)?)
    }
    
}