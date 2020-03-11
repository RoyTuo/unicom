package me.kuku.controller;

import me.kuku.bean.User;
import me.kuku.entity.PhoneLa;
import me.kuku.entity.Prize;
import me.kuku.repository.PhoneRepository;
import me.kuku.repository.PrizeRepository;
import me.kuku.service.CodeService;
import me.kuku.service.FlowService;
import me.kuku.service.LotteryService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class FlowController {
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    FlowService flowService;
    @Autowired
    PrizeRepository prizeRepository;
    @Autowired
    CodeService codeService;
    @Autowired
    User user;

    @RequestMapping("/")
    public String toIndex(Map<String, Object> map, @RequestParam(value = "type", required = false) String type){
        map.put("con", user.getHtmlCon());
        map.put("max", user.getMax());
        if ("pj".equals(type)){
            map.put("type", "pj");
        }else{
            map.put("type", "cj");
        }
        return "index";
    }

    @RequestMapping("/add")
    @ResponseBody
    public Integer addPhone(PhoneLa phoneLa, @RequestParam("code") String code, HttpServletRequest request){
        String myCode = (String) request.getSession().getAttribute("code");
        if (myCode != null){
            if (!myCode.equals(code.toUpperCase())) {
                return -3;
            }
        }else{
            return -3;
        }
        List<PhoneLa> list = phoneRepository.findAllByPhone(phoneLa.getPhone());
        if (!flowService.checkUnicom(phoneLa.getPhone())){
            return -2;
        }
        Long num = phoneRepository.count();
        if (num > user.getMax()){
            return -1;
        }
        if (list.size() != 0){
            return 0;
        }
        PhoneLa save = phoneRepository.save(phoneLa);
        return 1;
    }

    @RequestMapping("/query")
    @ResponseBody
    public List<Prize> queryPhone(@RequestParam("phone") String phone){
        List<Prize> prizeList = prizeRepository.findAllByPhone(phone);
        return prizeList;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String deletePhone(@RequestParam("phone") String phone){
        List<PhoneLa> list = phoneRepository.findAllByPhone(phone);
        if (list.size() == 0){
            return "未找到该号码";
        }else if (list.size() == 1){
            phoneRepository.delete(list.get(0));
            return "删除成功";
        }else {
            for (PhoneLa phoneLa : list){
                phoneRepository.delete(phoneLa);
            }
            return "删除成功";
        }
    }

    @RequestMapping("/get")
    @ResponseBody
    public Integer getCaptcha(@RequestParam("phone") String phone){
        Integer i = flowService.getCaptcha(phone);
        return i;
    }

    @RequestMapping("/verify")
    @ResponseBody
    public boolean verifyCaptcha(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha){
        boolean b = flowService.receiveFlow(phone, captcha);
        return b;
    }

    @GetMapping("/creatCode")
    @ResponseBody
    public void creatCode(HttpServletRequest request, HttpServletResponse response){
        codeService.creatVerifyCode(request, response);
    }
}
