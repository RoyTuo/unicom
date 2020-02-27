package me.kuku.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Service
public class CodeService {

    public void creatVerifyCode(HttpServletRequest request, HttpServletResponse response){
        int width = 100;
        int height = 32;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Color c = getRandomColor();
        g.setColor(c);
        g.fillRect(0, 0, width, height);
        String code = getRandomCode(4);
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60);
        session.setAttribute("code", code);
        g.setColor(Color.yellow);
        g.setFont(new Font("微软雅黑", Font.BOLD, 24));
        g.drawString(code, 15, 25);

        Random random = new Random();
        g.setColor(getRandomColor());
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        try {
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRandomCode(int len){
        String s = "ABCDEFGHIJKLNMOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++){
            sb.append(s.charAt((int) (Math.random() * (s.length() - 1))));
        }
        return sb.toString();
    }

    private Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(256),
                ran.nextInt(256), ran.nextInt(256));
        return color;
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        Random random = new Random();
        return new int[]{random.nextInt(255), random.nextInt(255), random.nextInt(255)};
    }
}
