package it.minusone.fastbuilder.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class a {
    public static void scanLoadedChunksForPressurePlates(World world) {
        if (world == null) return;

        for (Chunk chunk : world.getLoadedChunks()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < world.getMaxHeight(); y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (isPressurePlate(block))
                            Main.plateAboveLocations.add(block.getLocation());
                    }
                }
            }
        }
    }

    private static boolean isPressurePlate(Block block) {
        if (block == null) return false;
        Material type = block.getType();
        return type == Material.STONE_PLATE ||
                type == Material.WOOD_PLATE ||
                type == Material.GOLD_PLATE ||
                type == Material.IRON_PLATE;
    }

    public static String getPlayerSkinBase64(String player) {
        if (Bukkit.getOnlineMode()) {
            if (!player.equalsIgnoreCase("MHF_Question")) {
                Player playerA = Bukkit.getPlayer(player);
                if (playerA == null) return null;

                GameProfile profile = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) playerA).getProfile();
                Property texturesProperty = profile.getProperties().get("textures").iterator().next();
                return texturesProperty.getValue();
            } else {
                return "ewogICJ0aW1lc3RhbXAiIDogMTY4ODc3MzMxMzUwNSwKICAicHJvZmlsZUlkIiA6ICJiMTYzNmFlODcxNjI0ODBlOGMxNTIzZmZlM2E0YmU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUXVlc3Rpb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE4ZDU2OGVlYjFjMWFiNzM1ZWI3MzM3OTg1ODhlMzExMTNjZTlmZTY4YzlhY2E5YmZkMjI2YjE3NDVhNTY2YiIKICAgIH0KICB9Cn0=";
            }
        } else {
            return "ewogICJ0aW1lc3RhbXAiIDogMTY4ODc3MzMxMzUwNSwKICAicHJvZmlsZUlkIiA6ICJiMTYzNmFlODcxNjI0ODBlOGMxNTIzZmZlM2E0YmU1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUXVlc3Rpb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE4ZDU2OGVlYjFjMWFiNzM1ZWI3MzM3OTg1ODhlMzExMTNjZTlmZTY4YzlhY2E5YmZkMjI2YjE3NDVhNTY2YiIKICAgIH0KICB9Cn0=";
        }
    }


}
