package me.kuku.scheduled;

import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.BaiDuAIService;
import me.kuku.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${user.type}")
    int type;

    private List<PhoneLa> phoneAll;
    private Long count;

    @Scheduled(cron = "${user.cron1}")
    public void flow1() throws Exception{
        if (type == 1) {
            phoneAll = phoneRepository.findAll();
            count = phoneRepository.count();
            delPrize();
            if (count > 300) {
                run(phoneAll, 0, 300);
            } else {
                run(phoneAll, 0, Integer.parseInt(String.valueOf(count)));
            }
        }
    }

    @Scheduled(cron = "${user.cron2}")
    public void flow2() throws Exception{
        if (type == 1) {
            if (count > 600) {
                run(phoneAll, 300, 600);
            } else if (count > 300 && count < 600) {
                run(phoneAll, 300, Integer.parseInt(String.valueOf(count)));
            }
        }
    }

    @Scheduled(cron = "${user.cron3}")
    public void flow3() throws Exception{
        if (type == 1) {
            if (count > 600) {
                run(phoneAll, 600, Integer.parseInt(String.valueOf(count)));
            }
        }
    }

    @Scheduled(cron = "${user.cron}")
    public void flow() throws Exception{
        if (type == 0) {
            delPrize();
            phoneAll = phoneRepository.findAll();
            count = phoneRepository.count();
            run(phoneAll, 0, Integer.parseInt(String.valueOf(count)));
        }
    }

    public void run(List<PhoneLa> phoneAll, int start, int end){
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
            String gifts = lotteryService.run(phone, user, new BaiDuAIService());
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
