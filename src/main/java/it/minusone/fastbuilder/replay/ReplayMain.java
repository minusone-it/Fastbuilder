package it.minusone.fastbuilder.replay;

import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.recordable.entity.*;
import it.minusone.fastbuilder.replay.recordable.world.BlockRecordable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReplayMain implements Listener {
    private static final ReplayMain api = new ReplayMain();
    private final Set<Replay> recordingReplays = new HashSet<>();

    // --- instance ---
    private final Map<Class<? extends SaveRecordable>, Integer> recordableToId = new HashMap<>();
    private final Map<Integer, Class<? extends SaveRecordable>> idToRecordable = new HashMap<>();
    private Plugin plugin = null;

    public static ReplayMain getApi() {
        return api;
    }

    /**
     * Sets the plugin for the api
     *
     * @param plugin The plugin to set
     */
    public void initialize(Plugin plugin) {
        if (this.plugin != null) return;
        if (plugin == null) return;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new ReplayEvents(), plugin);

        // Entity Recordable
        registerRecordable(ArmorRecordable.class, 0x01);
        registerRecordable(DamageRecordable.class, 0x02);
        registerRecordable(EntityAddRecordable.class, 0x03);
        registerRecordable(EntityRemoveRecordable.class, 0x04);
        registerRecordable(GamemodeRecordable.class, 0x05);
        registerRecordable(ItemHeldRecordable.class, 0x06);
        registerRecordable(LocationRecordable.class, 0x07);
        registerRecordable(MessageRecordable.class, 0x08);
        registerRecordable(SneakRecordable.class, 0x09);
        registerRecordable(SwingRecordable.class, 0x0A);

        // World Recordable
        registerRecordable(BlockRecordable.class, 0x0B);
    }

    /**
     * @return The current plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    public void addRecording(Replay replay) {
        recordingReplays.add(replay);
    }

    public void removeRecording(Replay replay) {
        recordingReplays.remove(replay);
    }

    public Set<Replay> getRecordingReplays() {
        return recordingReplays;
    }

    public Map<Class<? extends SaveRecordable>, Integer> getRecordableToIdMap() {
        return recordableToId;
    }

    public Map<Integer, Class<? extends SaveRecordable>> getIdToRecordableMap() {
        return idToRecordable;
    }

    /**
     * Registers a recordable with an id.
     */
    public void registerRecordable(Class<? extends SaveRecordable> recordable, int id) throws IllegalStateException {
        if (idToRecordable.containsKey(id)) {
            throw new IllegalStateException("A recordable is already registered with that id.");
        }
        recordableToId.put(recordable, id);
        idToRecordable.put(id, recordable);
    }
}
