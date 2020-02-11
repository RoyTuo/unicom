package me.kuku;

import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.BaiDuAIService;
import me.kuku.service.FlowService;
import me.kuku.service.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;

@SpringBootTest
class UnicomApplicationTests {
    @Autowired
    PrizeRepository prizeRepository;

    @Autowired
    PhoneRepository phoneRepository;

    @Test
    void contextLoads() {
        PhoneLa byPhone = phoneRepository.findByPhone("17673373494");
        System.out.println(byPhone == null);
    }

}
