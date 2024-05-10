use std::error::Error;

use base64::{decode, encode};
use crypto::
{
    aes::{
        ecb_decryptor, ecb_encryptor, KeySize::KeySize128
    }, blockmodes::PkcsPadding, buffer::{RefReadBuffer, RefWriteBuffer}
};

use super::AESUtil;

impl AESUtil {

    /**
     * @descript 解密方法
     * @param text 密文
     * @param key 加密密钥
     */
    pub fn decrypt(text: &str, key: &str) -> Result<Vec<u8>, Box<dyn Error>>{
        let mut decrypt_fun = ecb_decryptor(
            KeySize128, 
            key.as_bytes(), 
            PkcsPadding
        );
        // 设置buffer长度
        let mut buffer = vec![0; decode(&text)?.len()];
        let mut out = RefWriteBuffer::new(&mut buffer);
        // 解密
        decrypt_fun.decrypt(&mut RefReadBuffer::new(&decode(text)?), &mut out, true).expect("decrypt error!");
        let decrypt_data: Vec<u8> = buffer.into_iter().filter(|v| *v != 0).collect();
        Ok(decrypt_data)
    }

    /**
     * @descript 加密方法
     * @param text 明文
     * @param key 加密密钥
     */
    pub fn encrypt(text: &str, key: &str) -> Result<String, Box<dyn Error>> {
        let bytes = text.as_bytes();
        let mut encrypt = ecb_encryptor(
            KeySize128,
            key.as_bytes(),
            PkcsPadding,
        );
        let mut read_buffer = RefReadBuffer::new(bytes);
        // 这里必须要保证数组大小大于16，因为aes加密的分组块大小以16为倍数
        let mut arr_size = bytes.len() * 4;
        if arr_size < 16 {
            arr_size = 16;
        }
        let mut result = vec![0; arr_size];
        let mut write_buffer = RefWriteBuffer::new(&mut result);
        encrypt.encrypt(&mut read_buffer, &mut write_buffer, true).expect("encrypt error!");

        let mut data_size = result.len();
        for i in (0..data_size - 1).rev() {
            if result[i] != 0 {
                // aes补码问题解决
                data_size = i + (16 - i % 16);
                break;
            }
        }
        Ok(encode(result[..data_size].to_vec()))
    }
}