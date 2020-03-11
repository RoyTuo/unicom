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

    @Scheduled(cron = "${user.cron}")
    public void flow() throws Exception{
        delPrize();
        BaiDuAIService baiDuAIService = new BaiDuAIService();
        List<PhoneLa> phoneAll = phoneRepository.findAll();
        String phone = "";
        for (PhoneLa phoneLa : phoneAll){
            phone = phoneLa.getPhone();
            int num = getAllFlow(phone);
            if (num >= 1000) continue;
            String gifts = lotteryService.run(phone, baiDuAIService);
            if (gifts == null || gifts.contains("联通")){
                //删除不是联通的号码
                phoneRepository.delete(phoneLa);
                continue;
            }
            prizeRepository.save(new Prize(null, phone, gifts, new Date(new java.util.Date().getTime())));
        }
    }

    public Integer getAllFlow(String phone){
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
        return num;
    }

    public void delPrize(){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1){
            prizeRepository.deleteAll();
        }
    }
}
