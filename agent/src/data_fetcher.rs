use sysinfo::{Components, Disks, Networks, System};

pub fn fetch_data() {
    let mut sys = System::new_all();

    // Refresh system information:
    sys.refresh_all();

    // RAM and swap information:
    println!("total memory: {} bytes", sys.total_memory());
    println!("used memory : {} bytes", sys.used_memory());
    println!("total swap  : {} bytes", sys.total_swap());
    println!("used swap   : {} bytes", sys.used_swap());

    // Number of CPUs:
    println!("NB CPUs: {}", sys.cpus().len());
    println!("{}", sys.global_cpu_usage());

    // Disk information:
    println!("=> disks:");
    let disks = Disks::new_with_refreshed_list();

    for disk in &disks {
        println!("{disk:?}");
    }

    // Network interfaces
    let networks = Networks::new_with_refreshed_list();

    println!("=> networks:");
    for (interface_name, data) in &networks {
        println!(
            "{interface_name}: {} B (down) / {} B (up)",
            data.total_received(),
            data.total_transmitted(),
        );
    }

    // Components temperature:
    let components = Components::new_with_refreshed_list();
    println!("=> components:");
    for component in &components {
        println!("{component:?}");
    }
}