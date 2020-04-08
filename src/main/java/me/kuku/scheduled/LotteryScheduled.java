package me.kuku.scheduled;

import com.baidu.aip.ocr.AipOcr;
import me.kuku.bean.Key;
import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.LotteryService;
import me.kuku.utils.LotteryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Component
@EnableScheduling
public class LotteryScheduled {

    @Autowired
    LotteryService lotteryService;
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    PrizeRepository prizeRepository;
    @Autowired
    User user;

    @Scheduled(cron = "${user.cron}")
    public void flow() throws Exception{
        LotteryUtil.delPrize(prizeRepository);
        List<PhoneLa> phoneAll = phoneRepository.findAll();
        int num = 0,round = 0;
        AipOcr aipOcr = null;
        List<Key> keyList = user.getKey();
        for (PhoneLa phoneLa : phoneAll){
            String phone = phoneLa.getPhone();
            //1000M流量以上不再抽奖
            int allFlow = LotteryUtil.getAllFlow(prizeRepository, phone);
            if (allFlow >= 1000) continue;
            //抽奖
            if (user.getType() == 1){
                if (num == 199) round++;
                if (round >= keyList.size()) break;
                Key key = keyList.get(round);
                if (num++ == 0)
                    aipOcr = new AipOcr(key.getApiId(), key.getApiKey(), key.getSecretKey());
            }
            String gifts = lotteryService.run(aipOcr, phone);
            if (gifts == null || gifts.contains("联通")){
                //删除不是联通的号码
                phoneRepository.delete(phoneLa);
                continue;
            }
            if (gifts.contains("超限")){
                round++;
                num = 0;
            }
            prizeRepository.save(new Prize(null, phone, gifts, new Date(new java.util.Date().getTime())));
        }
    }




}
