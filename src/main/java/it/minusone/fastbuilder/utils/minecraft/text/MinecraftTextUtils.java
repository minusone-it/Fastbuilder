package it.minusone.fastbuilder.utils.minecraft.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

public class MinecraftTextUtils {

    //from net.md_5.bungee.api.ChatColor
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");


    public static String componentToText(JsonObject o) {
        return componentToText(o, "", "");
    }

    private static String componentToText(JsonObject o, String s, String color) {
        if (o.has("color")) {
            color = "§" + toMinecraftColorCode(o.get("color").getAsString());
            s += color;
        }
        s += o.get("text").getAsString();
        if (o.has("extra"))
            for (JsonElement e : o.get("extra").getAsJsonArray())
                s = componentToText(e.getAsJsonObject(), s, color);
        return s;
    }


    //get the last color of the text but return empty if there's no color found.
    public static char getLastColorExact(String str) {
        for (int i = str.length() - 1; i >= 0; i--)
            if (str.charAt(i) == '§')
                return str.charAt(i + 1);
        return ' ';
    }

    public static String toMinecraftColorCode(String c) {
        switch (c.toLowerCase()) {
            case "black":
                return "0";
            case "dark_blue":
                return "1";
            case "dark_green":
                return "2";
            case "dark_aqua":
                return "3";
            case "dark_red":
                return "4";
            case "dark_purple":
                return "5";
            case "gold":
                return "6";
            case "gray":
                return "7";
            case "dark_gray":
                return "8";
            case "blue":
                return "9";
            case "green":
                return "a";
            case "aqua":
                return "b";
            case "red":
                return "c";
            case "light_purple":
                return "d";
            case "yellow":
                return "e";
            case "white":
                return "f";
        }
        return null;
    }
}
