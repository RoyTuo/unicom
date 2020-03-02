package me.kuku.scheduled;

import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.LotteryService;
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

//    @Autowired
//    LotteryService lotteryService;
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    PrizeRepository prizeRepository;
    @Autowired
    User user;


    @Scheduled(cron = "${user.cron}")
    public void flow() throws Exception{
        delPrize();
        List<PhoneLa> phoneAll = phoneRepository.findAll();
        String phone = "";
        for (PhoneLa phoneLa : phoneAll){
            phone = phoneLa.getPhone();
            List<Prize> prizes = prizeRepository.findAllByPhone(phone);
            int num = 0;
            for (Prize prize : prizes){
                String p = prize.getPrize();
                String[] split = p.split("；");
                for (String str : split){
                    if (str.contains("流量")){
                        String sss = str.substring(0, str.lastIndexOf("m"));
                        num += Integer.parseInt(sss);
                    }
                }
            }
            if (num >= 1000) continue;
            LotteryService lotteryService = new LotteryService();
            String gifts = lotteryService.run(phone);
            if (gifts == null || gifts.contains("没有抽奖次数了")){
                phoneRepository.delete(phoneLa);
                continue;
            }
            prizeRepository.save(new Prize(null, phone, gifts, new Date(new java.util.Date().getTime())));
        }
    }


    public void delPrize(){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1){
            prizeRepository.deleteAll();
        }
    }
}
