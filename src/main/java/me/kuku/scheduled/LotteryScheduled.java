package me.kuku.scheduled;

import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.BaiDuAIService;
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

    @Autowired
    LotteryService lotteryService;
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    PrizeRepository prizeRepository;
    @Autowired
    User user;

    @Scheduled(cron = "${user.cron1}")
    public void flow1() throws Exception{
        long count = phoneRepository.count();
        if (count > 300){
            run(0, 300);
        }else{
            run(0, Integer.parseInt(String.valueOf(count)));
        }
    }

    @Scheduled(cron = "${user.cron2}")
    public void flow2() throws Exception{
        long count = phoneRepository.count();
        if (count > 600){
            run(300, 600);
        }else{
            run(300, Integer.parseInt(String.valueOf(count)));
        }
    }

    @Scheduled(cron = "${user.cron3}")
    public void flow3() throws Exception{
        long count = phoneRepository.count();
        if (count > 600) {
            run(600, Integer.parseInt(String.valueOf(count)));
        }
    }




    public void run (int first, int last) throws Exception{
        List<PhoneLa> phoneAll = phoneRepository.findAll();
        String phone = "";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1){
            prizeRepository.deleteAll();
        }
        for (int i = first; i < last; i++){
            PhoneLa phoneLa = phoneAll.get(i);
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
            String gifts = lotteryService.run(phone, user, new BaiDuAIService());
            if (gifts == null || gifts.contains("没有抽奖次数了")){
                phoneRepository.delete(phoneLa);
                continue;
            }
            prizeRepository.save(new Prize(null, phone, gifts, new Date(new java.util.Date().getTime())));
        }
    }
}
