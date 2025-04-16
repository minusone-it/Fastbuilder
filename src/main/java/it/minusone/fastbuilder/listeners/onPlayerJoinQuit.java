package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class onPlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Main.getInstance().scoreboardManager.createScoreboard(e.getPlayer());
        if (Main.getInstance().getConfigManager().getModes() != null)
            e.getPlayer().performCommand("fb join " + Main.getInstance().getConfigManager().getModes().get(0));
        onPlayerClickEntity.injectPacketListener(e.getPlayer());
        if (Main.getInstance().getConfigManager().getCoinsOfPlayer(e.getPlayer()) == 0)
            Main.getInstance().getConfigManager().setCoinsOfPlayer(e.getPlayer(), 0);
        if (!Main.getInstance().getConfigManager().hasPlayerGotBlock(e.getPlayer(), new ItemStack(Material.SANDSTONE))) {
            Main.getInstance().getConfigManager().addBlockToPlayer(new ItemStack(Material.SANDSTONE), e.getPlayer());
            Main.getInstance().getConfigManager().addBlockToPlayer(new ItemStack(Material.RED_SANDSTONE), e.getPlayer());
            Main.getInstance().getConfigManager().setCurrentPlayerBlock(e.getPlayer(), new ItemStack(Material.SANDSTONE));
        }
        if (Main.getInstance().getConfigManager().getCurrentPlayerBlock(e.getPlayer()) == null)
            Main.getInstance().getConfigManager().setCurrentPlayerBlock(e.getPlayer(), new ItemStack(Material.SANDSTONE));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Main.getInstance().stats.get(p) != null) {
                for (Hologram h : Hologram.all) {
                    h.spawnFor(e.getPlayer());
                }
            }
        }
        Main.getInstance().playersInPracticeMode.put(e.getPlayer().getUniqueId(), false);

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        List<Location> playerBlocks = new ArrayList<>();
        if (Main.getInstance().placedBlocks.get(e.getPlayer().getUniqueId()) != null)
            Main.getInstance().placedBlocks.get(e.getPlayer().getUniqueId());
        if (Main.getInstance().placedPracticeBlocks.get(e.getPlayer().getUniqueId()) != null)
            playerBlocks.addAll(Main.getInstance().placedPracticeBlocks.get(e.getPlayer().getUniqueId()));
        for (Location blockLocation : playerBlocks)
            blockLocation.getBlock().setType(Material.AIR);
        Main.getInstance().placedBlocks.remove(e.getPlayer().getUniqueId());
        Main.getInstance().placedPracticeBlocks.remove(e.getPlayer().getUniqueId());
        Main.getInstance().scoreboardManager.removeScoreboard(e.getPlayer());
        Main.getInstance().removePlayerFromLeaderboard(e.getPlayer(), Main.getInstance().mapWherePlayerIn.get(e.getPlayer()).split("-")[0]);
        Main.getInstance().mapWherePlayerIn.remove(e.getPlayer());
        Main.getInstance().timer.stopTimer(e.getPlayer(), false);
        if (Main.getInstance().stats.get(e.getPlayer()) != null) {
            Main.getInstance().stats.get(e.getPlayer()).despawn();
            Main.getInstance().stats.get(e.getPlayer()).delete();
            Main.getInstance().stats.remove(e.getPlayer());
        }
        Main.getInstance().playersInPracticeMode.put(e.getPlayer().getUniqueId(), false);
    }

}
