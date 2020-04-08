package me.kuku.utils;

import java.awt.*;
import java.util.Random;

public class CodeUtil {

    public static Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(256),
                ran.nextInt(256), ran.nextInt(256));
        return color;
    }

    public static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    public static int[] getRandomRgb() {
        Random random = new Random();
        return new int[]{random.nextInt(255), random.nextInt(255), random.nextInt(255)};
    }
}
