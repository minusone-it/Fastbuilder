package it.minusone.fastbuilder.npc;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCManager {
    private static final HashMap<Player, NPC> npcs = new HashMap<>();
    private static final Map<Player, top.speedcubing.lib.bukkit.entity.NPC> npcsReplay = new HashMap<>();

    public static void setNPC(NPC npc, Player player) {
        npcs.put(player, npc);
    }

    public static void removeNPC(NPC npc, Player player) {
        getNPC(player).despawn(player);
        npcs.remove(player, npc);
    }
    public static List<NPC> getAllNPCs() {
        List<NPC> npcsList = new ArrayList<>();
        for (Map.Entry<Player, NPC> npc : npcs.entrySet())
            npcsList.add(npc.getValue());
        return npcsList;
    }
    public static NPC getNPC(Player player) {
        return npcs.get(player);
    }

    public static top.speedcubing.lib.bukkit.entity.NPC getReplayNPC(Player player) {
        return npcsReplay.get(player);
    }

    public static void setReplayNPC(top.speedcubing.lib.bukkit.entity.NPC npc, Player player) {
        npcsReplay.put(player, npc);
    }

    public static void removeReplayNPC(Player player) {
        npcsReplay.remove(player);
    }
}
