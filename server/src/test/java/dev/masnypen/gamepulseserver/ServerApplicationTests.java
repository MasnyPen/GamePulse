package dev.masnypen.gamepulseserver;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldDeserializeSysStatsFromAgentPayload() {
        String payload = """
                {"system":{"name":"Fedora Linux","kernel":"6.19.14-108.fc42.x86_64","os_version":"42","hostname":"MasnyPindolPC"},"memory":{"total":62.61,"used":28.11,"total_swap":8.0,"used_swap":0.0},"cpu":{"name":"Intel(R) Core(TM) i7-14700KF","threads":28,"temperature":62.0,"frequency":5.51,"used_percentage":6.07},"disks":[{"device":"/dev/nvme0n1p6","filesystem":"btrfs","total_space":1406.73,"used_space":31.92,"mounted_on":"/"},{"device":"/dev/nvme0n1p6","filesystem":"btrfs","total_space":1406.73,"used_space":31.92,"mounted_on":"/home"},{"device":"/dev/nvme0n1p5","filesystem":"ext4","total_space":0.95,"used_space":0.0,"mounted_on":"/boot"},{"device":"/dev/nvme0n1p1","filesystem":"vfat","total_space":0.09,"used_space":0.0,"mounted_on":"/boot/efi"},{"device":"/dev/nvme1n1p1","filesystem":"ext4","total_space":1876.72,"used_space":25.01,"mounted_on":"/mnt/f52e0bfc-3715-412f-9896-111c1d7497f4"}],"gpu":{"vendor":"Nvidia","model":"NVIDIA GeForce RTX 4070 SUPER","total_vram":11.99,"used_vram":1.97,"load_percentage":38.0,"temperature":37000.0}}
                """;

        SysStats stats = new Gson().fromJson(payload, SysStats.class);

        assertNotNull(stats);
        assertEquals("Fedora Linux", stats.getSystem().getName());
        assertEquals("42", stats.getSystem().getOsVersion());
        assertEquals(6.07, stats.getCpu().getUsedPercentage(), 0.0001);
        assertEquals(5, stats.getDisks().size());
        assertEquals(11.99, stats.getGpu().getTotalVram(), 0.0001);
        assertEquals(37000.0, stats.getGpu().getTemperature(), 0.0001);
    }

}
