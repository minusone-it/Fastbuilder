package it.minusone.fastbuilder.scoreboard.helpers.bukkit;

import it.minusone.fastbuilder.scoreboard.helpers.Scoreboard;
import it.minusone.fastbuilder.scoreboard.helpers.bukkit.util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BPlayerBoard implements PlayerBoard<String, Integer, String> {

    private final Player player;
    private final String name;
    private org.bukkit.scoreboard.Scoreboard scoreboard;
    private Objective objective;
    private Objective buffer;

    private Map<Integer, String> lines = new HashMap<>();
    private boolean deleted = false;

    public BPlayerBoard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, String name) {
        this.player = player;
        this.scoreboard = scoreboard;

        if (this.scoreboard == null) {
            org.bukkit.scoreboard.Scoreboard sb = player.getScoreboard();
            if (sb == null || sb == Bukkit.getScoreboardManager().getMainScoreboard())
                sb = Bukkit.getScoreboardManager().getNewScoreboard();
            this.scoreboard = sb;
        }

        this.name = name;

        String subName = player.getName().length() <= 14
                ? player.getName()
                : player.getName().substring(0, 14);

        this.objective = this.scoreboard.getObjective("sb" + subName);
        this.buffer = this.scoreboard.getObjective("bf" + subName);

        if (this.objective == null)
            this.objective = this.scoreboard.registerNewObjective("sb" + subName, "dummy");
        if (this.buffer == null)
            this.buffer = this.scoreboard.registerNewObjective("bf" + subName, "dummy");

        this.objective.setDisplayName(name);
        sendObjective(this.objective, ObjectiveMode.CREATE);
        sendObjectiveDisplay(this.objective);

        this.buffer.setDisplayName(name);
        sendObjective(this.buffer, ObjectiveMode.CREATE);

        this.player.setScoreboard(this.scoreboard);
    }

    public void set(String name, Integer score) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        String oldName = this.lines.get(score);
        if (name.equals(oldName)) return;

        this.lines.entrySet()
                .removeIf(entry -> entry.getValue().equals(name));

        if (oldName != null) {
            sendScore(this.buffer, oldName, score, true);
            sendScore(this.buffer, name, score, false);
            swapBuffers();
            sendScore(this.buffer, oldName, score, true);
            sendScore(this.buffer, name, score, false);
        } else {
            sendScore(this.objective, name, score, false);
            sendScore(this.buffer, name, score, false);
        }

        this.lines.put(score, name);
    }

    private void swapBuffers() {
        sendObjectiveDisplay(this.buffer);
        Objective temp = this.buffer;
        this.buffer = this.objective;
        this.objective = temp;
    }

    private void sendObjective(Objective obj, ObjectiveMode mode) {
        try {
            Object objHandle = NMS.getHandle(obj);
            Object packetObj = NMS.PACKET_OBJ.newInstance(objHandle, mode.ordinal());
            NMS.sendPacket(packetObj, player);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
        }
    }

    private void sendObjectiveDisplay(Objective obj) {
        try {
            Object objHandle = NMS.getHandle(obj);
            Object packet = NMS.PACKET_DISPLAY.newInstance(1, objHandle);
            NMS.sendPacket(packet, player);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void sendScore(Objective obj, String name, int score, boolean remove) {
        try {
            Object sbHandle = NMS.getHandle(scoreboard);
            Object objHandle = NMS.getHandle(obj);

            Object sbScore = NMS.SB_SCORE.newInstance(sbHandle, objHandle, name);
            NMS.SB_SCORE_SET.invoke(sbScore, score);

            Map scores = (Map) NMS.PLAYER_SCORES.get(sbHandle);

            if (remove) {
                if (scores.containsKey(name))
                    ((Map) scores.get(name)).remove(objHandle);
            } else {
                if (!scores.containsKey(name))
                    scores.put(name, new HashMap());

                ((Map) scores.get(name)).put(objHandle, sbScore);
            }

            Object packet = remove
                    ? NMS.PACKET_SCORE_REMOVE.newInstance(name, objHandle)
                    : NMS.PACKET_SCORE.newInstance(sbScore);

            NMS.sendPacket(packet, player);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
        }
    }

    private void remove(Integer score) {
        if (this.deleted)
            throw new IllegalStateException("The PlayerBoard is deleted!");

        String name = this.lines.get(score);
        if (name == null) return;

        this.scoreboard.resetScores(name);
        this.lines.remove(score);
    }

    public void delete() {
        if (this.deleted) return;

        Scoreboard.instance().removeBoard(player);

        sendObjective(this.objective, ObjectiveMode.REMOVE);
        sendObjective(this.buffer, ObjectiveMode.REMOVE);

        this.objective.unregister();
        this.objective = null;

        this.buffer.unregister();
        this.buffer = null;

        this.lines = null;
        this.deleted = true;
    }

    private enum ObjectiveMode {CREATE, REMOVE, UPDATE}
}
