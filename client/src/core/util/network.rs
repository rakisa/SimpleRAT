use std::{error::Error, fs::File, io::Write, net::{SocketAddr, UdpSocket}, path::Path};

use http_req::{request::{self, Request}, response::Response, uri::Uri};

use crate::core::{common::{config::Config, network_type::ConnectType}, model::HttpResult};

use super::{AESUtil, NetWork};

// 网络工具


impl NetWork{

    // 自动检查并选择进行通信的协议
    pub fn auto_switch_connect(&self) -> ConnectType {
        if Self::chk_http_con(&self.config.host, self.config.use_https) {
            ConnectType::HTTP
        } else if Self::chk_udp_con(&self.config.host, self.config.udp_port) {
            ConnectType::UDP
        } 
        // else if Self::chk_tcp_con(&self.config.host, self.config.tcp_port) {
        //     ConnectType::TCP
        // } 
        else {
            ConnectType::CLOSE
        }
    }

    // 检查tcp协议的连通性
    // fn chk_tcp_con(host: &str, port: u16) -> bool {
    //     let addr = format!("{}:{}", host, port).parse::<SocketAddr>().unwrap();
    //     TcpStream::connect_timeout(&addr, Duration::from_secs(5)).is_ok()
    // }

    // 检查udp协议的连通性
    fn chk_udp_con(host: &str, port: u16) -> bool {
        let addr = format!("{}:{}", host, port).parse::<SocketAddr>().unwrap();
        println!("{:?}", addr);
        // 使用随机本地端口进行通信
        let socket = UdpSocket::bind("0.0.0.0:0").unwrap();
        match socket.connect(addr) {
            Ok(_) => true,
            Err(_) => false,
        }
    }

    // 检查http协议的连通性
    fn chk_http_con(host: &str, use_https: bool) -> bool {
        let url = if use_https {
            format!("https://{}/ping", host)
        } else {
            format!("http://{}/ping", host)
        };
        let mut res = Vec::new();
        match request::get(url, &mut res) {
            Ok(_) => match String::from_utf8(res) {
                Ok(res) => res.eq("pong"),
                Err(_) => false,
            }
            Err(_) => false,
        }
    }

    /**
     * @descript 发送http请求的方法
     * @param request_uri: 请求的url地址
     * @param headers: 构造的请求头
     * @param request_body: 构造的请求体
     * @param result: 可变借用result在使用后会返回
     */
    pub fn post(&self, request_uri: &str, request_body: &str, result: &mut Vec<u8>) -> Result<Response, Box<dyn Error>> {
        let encrypt_uri = AESUtil::encrypt(request_uri, &self.config.key)?;
        let encrypt_data = AESUtil::encrypt(request_body, &self.config.key)?;

        let uri = if self.config.use_https {
            format!("https://{}/{}", self.config.host, encrypt_uri)
        } else {
            format!("http://{}/{}", self.config.host, encrypt_uri)
        };
        let uri = Uri::try_from(uri.as_ref()).unwrap();
        let response = Request::new(&uri)
            .method(request::Method::POST)
            .header("Content-Length", &encrypt_data.len())
            .header("Content-Type", "application/json")
            .header("token", &self.config.token)
            .body(encrypt_data.as_bytes())
            .send(result)?;
        Ok(response)
    }

    
    /**
     * udp协议和服务器进行交互
     * @ param data 任务数据
     */
    pub fn upd_send_data(&self, data: String) -> Result<String, Box<dyn Error>>{
        let encrypt_data = AESUtil::encrypt(&data, &self.config.key)?;
        let server_addr = format!("{}:{}", self.config.host, self.config.udp_port).parse::<SocketAddr>()?;
        let socket = UdpSocket::bind("0.0.0.0:0")?;
        // 发送加密数据
        socket.send_to(encrypt_data.as_bytes(), server_addr)?;
        // 接收来自Java服务器的响应消息
        let mut buf = [0; 4096];
        let (amt, _) = socket.recv_from(&mut buf)?;
        Ok(String::from_utf8((&buf[..amt]).to_vec())?)
    }

    /**
     * upd协议下载文件数据
     * @ param data 要下载文件的任务数据
     * @ param path 下载文件名
     */
    pub fn udp_download_data(&self, data: String, path: String) -> Result<(), Box<dyn Error>> {
        let encrypt_data = AESUtil::encrypt(&data, &self.config.key)?;
        let server_addr = format!("{}:{}", self.config.host, self.config.udp_port).parse::<SocketAddr>()?;
        let socket = UdpSocket::bind("0.0.0.0:0")?;
        // 发送加密数据
        socket.send_to(encrypt_data.as_bytes(), server_addr)?;
        let mut buffer = [0; 4096];
        let filename = Path::new(&path).file_name().unwrap().to_str().unwrap();
        println!("file:{:?}", filename);
        let mut file = File::options().append(true).create(true).open(filename)?;
        // 接收来自Java服务器的响应消息
        loop {
            let (amt, _) = socket.recv_from(&mut buffer)?;
            if amt == 0 {
                break;
            }
            let _ = file.write(&buffer[..amt]);
        }
        Ok(())
    }

    /**
     * @ description 解密响应包数据为字符串
     * @ param response 加密的响应包数据
     */
    pub fn decrypt(&self, response: Vec<u8>) -> Result<String, Box<dyn Error>> {
        let http_response = String::from_utf8(response)?;
        let http_response: HttpResult = serde_json::from_str(&http_response)?;
        match AESUtil::decrypt(&http_response.data, &self.config.key) {
            Ok(data) => Ok(String::from_utf8(data)?),
            Err(_) => panic!("解密失败"),
        }
    }

    pub fn init(config: Config) -> Self {
        NetWork {
            config,
        }
    }


}