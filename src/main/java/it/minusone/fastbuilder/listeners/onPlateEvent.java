package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class onPlateEvent extends BukkitRunnable {

    private final Set<UUID> alreadyTriggered = new HashSet<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean onGoldPlate = isOnActivatedAreaOfGoldPlate(player);

            if (onGoldPlate) {
                if (alreadyTriggered.contains(player.getUniqueId())) {
                    continue;
                }

                alreadyTriggered.add(player.getUniqueId());
                handleScore(player);
            } else {
                alreadyTriggered.remove(player.getUniqueId());
            }
        }
    }

    private void handleScore(Player player) {
        Boolean practiceMode = Main.getInstance().playersInPracticeMode.get(player.getUniqueId());
        if (practiceMode == null) {
            return;
        }

        if (!practiceMode) {
            if (Main.getInstance().timer.scoreTime(player, false)) {
                try {
                    Main.getInstance().getConfigManager().addAttempt(player);
                    Main.getInstance().getConfigManager().addSuccessfulAttempt(player);

                    String[] holoLines = Main.getInstance().getConfigManager().getHoloLines(player);
                    for (int i = 0; i < holoLines.length; i++) {
                        Main.getInstance().stats.get(player).setName(i, holoLines[i]);
                    }
                } catch (Exception ignored) {
                }
            }
        } else {
            Main.getInstance().timer.scoreTime(player, true);
        }
    }

    private boolean isOnActivatedAreaOfGoldPlate(Player player) {
        AxisAlignedBB playerBB = ((CraftPlayer) player).getHandle().getBoundingBox();

        int minBlockX = floor(playerBB.a);
        int maxBlockX = floor(playerBB.d);
        int minBlockY = floor(playerBB.b) - 1;
        int maxBlockY = floor(playerBB.e);
        int minBlockZ = floor(playerBB.c);
        int maxBlockZ = floor(playerBB.f);

        for (int x = minBlockX; x <= maxBlockX; x++) {
            for (int y = minBlockY; y <= maxBlockY; y++) {
                for (int z = minBlockZ; z <= maxBlockZ; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);

                    if (block.getType() != Material.GOLD_PLATE) {
                        continue;
                    }

                    if (intersects(playerBB, getGoldPlateTriggerBox(block))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private AxisAlignedBB getGoldPlateTriggerBox(Block block) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();

        return new AxisAlignedBB(
                x + 0.125D,
                y,
                z + 0.125D,
                x + 0.875D,
                y + 0.25D,
                z + 0.875D
        );
    }

    private boolean intersects(AxisAlignedBB first, AxisAlignedBB second) {
        return first.d > second.a && first.a < second.d
                && first.e > second.b && first.b < second.e
                && first.f > second.c && first.c < second.f;
    }

    private int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }
}
