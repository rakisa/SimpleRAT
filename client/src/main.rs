// #![windows_subsystem = "windows"]
use std::error::Error;

use core::application::Application;

mod core;

fn main() -> Result<(), Box<dyn Error>> {
    Ok(Application::run()?)
}