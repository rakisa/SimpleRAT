// use std::error::Error;

// use crate::core::{common::config::Config, model::Tasks, util::NetWork};

// use super::{TcpAPI, API};

// impl API for TcpAPI {
//     fn get_aes_key(&self, config: &Config) -> Result<String, Box<dyn Error>> {
//         todo!()
//     }

//     fn send_online_information(&self, network: &NetWork) -> Result<(String, u64), Box<dyn Error>> {
//         todo!()
//     }

//     fn get_tasklist(&self, network: &NetWork) -> Result<Tasks, Box<dyn Error>> {
//         todo!()
//     }

//     fn send_execution_result(&self, network: &NetWork, id: &str, data: &str){
//         todo!()
//     }

//     fn upload_binary(&self, network: &NetWork, id: &str, data: Vec<u8>) {
//         todo!()
//     }

//     fn download_binary(&self, network: &NetWork, id: &str, path: String) -> Result<(), Box<dyn Error>> {

//         Ok(())
//     }
// }