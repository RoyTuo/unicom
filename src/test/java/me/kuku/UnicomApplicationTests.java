package me.kuku;

import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;


@SpringBootTest
class UnicomApplicationTests {
    @Autowired
    LotteryService lotteryService;
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    PrizeRepository prizeRepository;

    @Test
    void contextLoads() {
    }

}
