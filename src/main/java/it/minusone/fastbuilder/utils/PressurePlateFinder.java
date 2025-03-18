package it.minusone.fastbuilder.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PressurePlateFinder {
    public static List<Location> getAirAbovePlates(JavaPlugin plugin, String worldName) {
        World world = Bukkit.getWorld(worldName);
        List<Location> airBlocks = new ArrayList<>();

        if (world == null) {
            plugin.getLogger().warning("Il mondo " + worldName + " non esiste o non è caricato!");
            return airBlocks;
        }

        for (Chunk chunk : world.getLoadedChunks()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < world.getMaxHeight() - 5; y++) { // -5 per evitare IndexOutOfBounds
                        Block block = chunk.getBlock(x, y, z);
                        if (isPressurePlate(block.getType())) {
                            // Aggiungi le posizioni dei blocchi d'aria sopra
                            for (int i = 1; i <= 5; i++) {
                                Block above = block.getRelative(0, i, 0);
                                if (above.getType() == Material.AIR) {
                                    airBlocks.add(above.getLocation());
                                    System.out.println(block.getLocation());
                                }
                            }
                        }
                    }
                }
            }
        }

        return airBlocks;
    }

    private static boolean isPressurePlate(Material material) {
        return material == Material.WOOD_PLATE || material == Material.STONE_PLATE;
    }
}
