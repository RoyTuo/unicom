package me.kuku;

import me.kuku.bean.Key;
import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.scheduled.LotteryScheduled;
import me.kuku.service.BaiDuAIService;
import me.kuku.service.FlowService;
import me.kuku.service.LotteryService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;


@SpringBootTest
class UnicomApplicationTests {
    @Test
    void contextLoads(){

    }

}
