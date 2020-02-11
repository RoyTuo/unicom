package me.kuku;

import me.kuku.service.BaiDuAIService;
import me.kuku.service.FlowService;
import me.kuku.service.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

@SpringBootTest
class UnicomApplicationTests {

    @Autowired
    FlowService flowService;

    @Autowired
    LotteryService lotteryService;

    @Autowired
    BaiDuAIService baiDuAIService;

    @Test
    void contextLoads() {
        String token = baiDuAIService.getToken();
        System.out.println(token);
    }

}
