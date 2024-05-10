use obfstr::obfstr;

#[derive(Debug)]
pub struct Config {

    pub host: String,
    pub tcp_port: u16,
    pub udp_port: u16,
    pub use_https: bool,
    pub key: String,
    pub time: u64,
    pub token: String

}

impl Config {
    pub fn init() -> Self {
        Config {
            host: obfstr!("127.0.0.1").to_string(),
            tcp_port: 8080,
            udp_port: 530,
            use_https: false,
            key: String::new(),
            time: 30,
            token: String::new()
        }
    }
}