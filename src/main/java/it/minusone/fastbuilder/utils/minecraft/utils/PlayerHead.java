package it.minusone.fastbuilder.utils.minecraft.utils;


import org.bukkit.ChatColor;

import java.awt.*;

public class PlayerHead {


    private static final Color[] colors = new Color[]{
            new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255),
            new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255)
    };


    private static ChatColor getClosestChatColor(Color color) {
        if (color.getAlpha() < 128)
            return null;
        int j = 0;
        double d1 = 1000000000;
        for (int i = 0; i < 16; i++) {
            double d2 = Math.abs(color.getBlue() - colors[i].getBlue()) + Math.abs(color.getRed() - colors[i].getRed()) + Math.abs(color.getGreen() - colors[i].getGreen());
            if (d2 < d1) {
                d1 = d2;
                j = i;
            }
        }
        return ChatColor.values()[j];
    }
}
