package it.minusone.fastbuilder.utils;

import it.minusone.fastbuilder.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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
}
