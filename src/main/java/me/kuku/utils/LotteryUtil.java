package me.kuku.utils;

import me.kuku.entity.Prize;
import me.kuku.repository.PrizeRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LotteryUtil {

    public static Integer getAllFlow(PrizeRepository prizeRepository, String phone){
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

    public static void delPrize(PrizeRepository prizeRepository){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1){
            prizeRepository.deleteAll();
        }
    }
}
