use super::DeviceUtil;
use std::error::Error;

impl DeviceUtil {
    /**
     * @description 截图
     * @return 图片的二进制数据
     */
    #[cfg(windows)]
    pub fn screen_shot() -> Result<Vec<u8>, Box<dyn Error>> {
    	use xcap::Monitor;
    	
        let monitors = Monitor::all()?;
        let tmp = "tmp.png";
        let monitor = monitors.get(0).unwrap();
        let image = monitor.capture_image()?;
        image.save(tmp)?;
        match std::fs::read(tmp) {
            Ok(binary) => {
                std::fs::remove_file(tmp)?;
                Ok(binary)
            },
            Err(_) => Ok(vec![])
        }
    }
    #[cfg(not(windows))]
    pub fn screen_shot() -> Result<Vec<u8>, Box<dyn Error>> {
        Ok(vec![])
    }
}
