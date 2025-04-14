package it.minusone.fastbuilder.scoreboard.helpers.bukkit.util;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NMS {

    public static final Field PLAYER_SCORES;
    public static final Constructor<?> PACKET_SCORE_REMOVE;
    public static final Constructor<?> PACKET_SCORE;
    public static final Constructor<?> SB_SCORE;
    public static final Method SB_SCORE_SET;
    public static final Constructor<?> PACKET_OBJ;
    public static final Constructor<?> PACKET_DISPLAY;
    public static final Field PLAYER_CONNECTION;
    public static final Method SEND_PACKET;
    private static final Map<Class<?>, Method> HANDLES = new HashMap<>();

    static {
        Field playerScores = null;
        Constructor<?> packetScoreRemove = null;
        Constructor<?> packetScore = null;
        Constructor<?> sbScore = null;
        Method sbScoreSet = null;
        Constructor<?> packetObj = null;
        Constructor<?> packetDisplay = null;
        Field playerConnection = null;
        Method sendPacket = null;

        try {
            String nms = "net.minecraft.server.v1_8_R3";

            Class<?> packetScoreClass = Class.forName(nms + ".PacketPlayOutScoreboardScore");
            Class<?> packetDisplayClass = Class.forName(nms + ".PacketPlayOutScoreboardDisplayObjective");
            Class<?> packetObjClass = Class.forName(nms + ".PacketPlayOutScoreboardObjective");

            Class<?> scoreClass = Class.forName(nms + ".ScoreboardScore");
            Class<?> sbClass = Class.forName(nms + ".Scoreboard");
            Class<?> objClass = Class.forName(nms + ".ScoreboardObjective");

            Class<?> playerClass = Class.forName(nms + ".EntityPlayer");
            Class<?> playerConnectionClass = Class.forName(nms + ".PlayerConnection");
            Class<?> packetClass = Class.forName(nms + ".Packet");

            sbScore = scoreClass.getConstructor(sbClass, objClass, String.class);
            sbScoreSet = scoreClass.getMethod("setScore", int.class);
            packetObj = packetObjClass.getConstructor(objClass, int.class);
            packetDisplay = packetDisplayClass.getConstructor(int.class, objClass);
            packetScore = packetScoreClass.getConstructor(scoreClass);
            packetScoreRemove = packetScoreClass.getConstructor(String.class, objClass);

            playerScores = sbClass.getDeclaredField("playerScores");
            playerScores.setAccessible(true);

            playerConnection = playerClass.getField("playerConnection");
            sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);

        } catch (Exception e) {
            e.printStackTrace(); // Debug only
        }

        PLAYER_SCORES = playerScores;
        PACKET_SCORE_REMOVE = packetScoreRemove;
        PACKET_SCORE = packetScore;
        SB_SCORE = sbScore;
        SB_SCORE_SET = sbScoreSet;
        PACKET_OBJ = packetObj;
        PACKET_DISPLAY = packetDisplay;
        PLAYER_CONNECTION = playerConnection;
        SEND_PACKET = sendPacket;
    }

    public static Object getHandle(Object obj) {
        try {
            Class<?> clazz = obj.getClass();

            if (!HANDLES.containsKey(clazz)) {
                Method method = clazz.getDeclaredMethod("getHandle");
                method.setAccessible(true);
                HANDLES.put(clazz, method);
            }

            return HANDLES.get(clazz).invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static void sendPacket(Object packet, Player... players) {
        for (Player p : players) {
            try {
                Object playerConnection = PLAYER_CONNECTION.get(getHandle(p));
                SEND_PACKET.invoke(playerConnection, packet);
            } catch (Exception ignored) {
            }
        }
    }
}
