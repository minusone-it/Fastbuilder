package it.minusone.fastbuilder.replay;

import it.minusone.fastbuilder.replay.entity.EntityInfo;
import it.minusone.fastbuilder.replay.enums.PlaybackState;
import it.minusone.fastbuilder.replay.event.PlaybackEndEvent;
import it.minusone.fastbuilder.replay.event.PlaybackStateChangeEvent;
import it.minusone.fastbuilder.replay.npc.ActualNPC;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import it.minusone.fastbuilder.replay.recordable.UndoRecordable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Use this class to replay a replay to a player
 */
public class ReplayPlayback {
    private final Plugin plugin;
    private final Replay replay;
    private final Location origin;
    private final HashMap<Integer, ReplayNPC> npcs = new HashMap<>();
    private final Set<Player> listeners = new HashSet<>();
    private int task = -2;
    private int currentTick = 0;
    private PlaybackState state = PlaybackState.NONE;

    public ReplayPlayback(Plugin plugin, Replay replay, Location origin) {
        this.plugin = plugin;
        this.replay = replay;
        this.origin = origin;
    }

    public void addListener(Player player) {
        listeners.add(player);
    }

    public ReplayNPC getNPC(int id) {
        return npcs.getOrDefault(id, null);
    }

    public Location getOrigin() {
        return origin;
    }

    public Replay getReplay() {
        return replay;
    }

    private void setState(PlaybackState state) {
        this.state = state;
        Bukkit.getPluginManager().callEvent(new PlaybackStateChangeEvent(this));
    }

    /**
     * Spawns a npc in the replay playback
     *
     * @param entityId Entity id of the new npc
     * @param info     The info of the npc
     */
    public void spawnNpc(int entityId, EntityInfo info) {
        // Note: This can be changed for another npc library if needed, or native npc packets.
        ReplayNPC npc = new ActualNPC(this, info.getName());

        for (Player listener : listeners) {
            npc.addListener(listener);
        }

        if (info.hasSkin()) {
            npc.setSkin(info.getSkin());
        }

        npc.setLocation(origin);
        npc.spawn();

        npcs.put(entityId, npc);
    }

    /**
     * Removes a npc in the playback
     *
     * @param id The entity id of the npc
     */
    public void removeNPC(int id) {
        ReplayNPC npc = npcs.get(id);
        npc.remove();
        npcs.remove(id);
    }

    /**
     * @return The players currently watching the playback
     */
    public Set<Player> getListeners() {
        return listeners;
    }

    /**
     * @return The current tick the playback is at
     */
    public int getCurrentTick() {
        return currentTick;
    }

    /**
     * Setups the replay npcs and other stuff
     */
    public void setup() {
        currentTick = 0;

        for (Integer id : replay.getEntityInfo().keySet()) {
            EntityInfo info = replay.getEntityInfo().get(id);
            spawnNpc(id, info);
        }

        setState(PlaybackState.NONE);
        playTick();
    }

    /**
     * Starts playing the replay
     */
    public void play() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (currentTick >= replay.getLength()) {
                pause();
                return;
            }
            nextTick();
        }, 0L, 1L);
        setState(PlaybackState.PLAYING);
    }

    /**
     * Pauses the replay at its current state
     */
    public void pause() {
        if (task != -2) {
            Bukkit.getScheduler().cancelTask(task);
            task = -2;
        }
        setState(PlaybackState.PAUSED);
    }

    /**
     * Ends the replay completely
     */

    public void sendTickInfoToActionBar() {
        int currentTick = getCurrentTick();
        int maxTick = getReplay().getLength();

        double currentSeconds = currentTick / 20.0;
        double maxSeconds = maxTick / 20.0;

        DecimalFormat decimalFormat = new DecimalFormat("0.000");

        for (Player player : getListeners()) {
            try {
                sendActionBar(player, "§3" + decimalFormat.format(currentSeconds) + " / " + decimalFormat.format(maxSeconds - 0.050));
            } catch (Exception e) {

            }
        }
    }

    public void sendActionBar(Player player, String message) throws Exception {
        String v = "v1_8_R3";
        message = ChatColor.translateAlternateColorCodes('&', message);
        Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer");
        Object p = c1.cast(player);
        Object ppoc;
        Class<?> c4 = Class.forName("net.minecraft.server." + v + ".PacketPlayOutChat");
        Class<?> c5 = Class.forName("net.minecraft.server." + v + ".Packet");

        Class<?> c2 = Class.forName("net.minecraft.server." + v + ".ChatComponentText");
        Class<?> c3 = Class.forName("net.minecraft.server." + v + ".IChatBaseComponent");
        Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
        ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);

        Method getHandle = c1.getDeclaredMethod("getHandle");
        Object handle = getHandle.invoke(p);

        Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
        Object playerConnection = fieldConnection.get(handle);

        Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
        sendPacket.invoke(playerConnection, ppoc);
    }

    public void end() {
        if (state == PlaybackState.PLAYING) {
            pause();
        }

        // Replay entire replay, removing everything
        for (List<Recordable> recordableList : getReplay().getReplayData().values()) {
            for (Recordable recordable : recordableList) {
                if (recordable instanceof UndoRecordable) {
                    ((UndoRecordable) recordable).undo(this);
                }
            }
        }

        synchronized (npcs) {
            for (int id : npcs.keySet()) {
                removeNPC(id);
            }
            npcs.clear();
        }

        Bukkit.getPluginManager().callEvent(new PlaybackEndEvent(this));
        setState(PlaybackState.ENDED);
    }

    /**
     * Plays one tick of the replay
     */
    public void nextTick() {
        currentTick++;
        playTick();
        sendTickInfoToActionBar();
    }

    /**
     * Plays the current tick of the replay.
     * Not recommended to use, use nextTick or lastTick instead.
     */
    public void playTick() {
        if (replay.getReplayData().containsKey(currentTick)) {
            List<Recordable> data = replay.getReplayData().get(currentTick);
            for (Recordable recordable : data) {
                recordable.play(this);
            }
        }
        sendTickInfoToActionBar();
    }

    /**
     * Reverses one tick of the replay
     */
    public void lastTick() {
        // undo current tick
        if (replay.getReplayData().containsKey(currentTick)) {
            List<Recordable> data = replay.getReplayData().get(currentTick);
            for (Recordable recordable : data) {
                if (recordable instanceof UndoRecordable) {
                    ((UndoRecordable) recordable).undo(this);
                }
            }
        }
        // remove one tick
        currentTick--;
        // play next tick
        if (replay.getReplayData().containsKey(currentTick)) {
            List<Recordable> data = replay.getReplayData().get(currentTick);
            for (Recordable recordable : data) {
                recordable.play(this);
            }
        }
        sendTickInfoToActionBar();
    }

}
