package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.ReplayGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.ReplayManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.List;

public class onPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Player player = event.getPlayer();
                String timeFormatted = Main.getInstance().timer.getTimeFormatted(player);
                double time = Main.getInstance().timer.getTime(player);
                if (!timeFormatted.equalsIgnoreCase("0.000")) {
                    Main.getInstance().timer.stopTimer(player);
                    player.sendTitle(ChatColor.GREEN + "Time: " + ChatColor.YELLOW + timeFormatted, "+" + ChatColor.YELLOW + "10 coins!");
                    double PB = Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + Main.getInstance().modesWherePlayerIn.get(player), 0.000);
                    boolean equalTime = PB == time;
                    boolean newPB = time < PB;
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                    player.sendMessage("§8----------------------------------");
                    if (newPB) {
                        player.sendMessage("§b§lNEW PERSONAL BEST!");
                        player.sendMessage("");
                        World world = player.getWorld();
                        Location loc = player.getLocation();

                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            Firework firework = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
                            FireworkMeta meta = firework.getFireworkMeta();
                            FireworkEffect effect = FireworkEffect.builder()
                                    .flicker(false)
                                    .trail(true)
                                    .withColor(Color.RED)
                                    .withFade(Color.ORANGE)
                                    .with(FireworkEffect.Type.BALL)
                                    .build();
                            meta.addEffect(effect);
                            meta.setPower(1);
                            firework.setFireworkMeta(meta);
                        });
                    }
                    player.sendMessage("§7You've finished the course!");
                    player.sendMessage("");
                    player.sendMessage("§aPB: §e§l" + this.getFormat().format(PB) + " §8| " + (equalTime ? "§7§l+" : newPB ? "§b§l" :"§e§l+") + this.getFormat().format(time - PB));
                    player.sendMessage("§aTime: §e§l" + this.getFormat().format(time));
                    player.sendMessage("§8----------------------------------");
                    Main.getInstance().saveBestTime(player.getUniqueId(), Main.getInstance().modesWherePlayerIn.get(player), time);
                    Main.getInstance().scoreboardManager.updateTimer(player, timeFormatted, Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + Main.getInstance().modesWherePlayerIn.get(player), 0.000));
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().modesWherePlayerIn.get(player), Main.getInstance().islandWherePlayerIn.get(player)));
                        }
                    }, 40L);
                }
                List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                if (playerBlocks != null) {
                    for (Location blockLocation : playerBlocks) {
                        if (blockLocation.getBlock().getType() == Material.SANDSTONE)
                            blockLocation.getBlock().setType(Material.AIR);
                    }
                    Main.getInstance().placedBlocks.remove(player.getUniqueId());
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Island Selector"))
                event.getPlayer().openInventory(new IslandSelectorGUI(event.getPlayer()).getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Settings"))
                event.getPlayer().openInventory(new SettingsGUI().getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Replay"))
                event.getPlayer().openInventory(new ReplayGUI(event.getPlayer().getUniqueId()).getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Cancel")) {
                if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId())) {
                    ReplayManager.endReplay(event.getPlayer());
                    event.getPlayer().getInventory().clear();
                    event.getPlayer().getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
                    event.getPlayer().getInventory().setItem(1, new ItemStack(Material.SANDSTONE, 64));
                    event.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                    event.getPlayer().getInventory().setItem(4, new IslandSelector());
                    event.getPlayer().getInventory().setItem(6, new ReplayItem());
                    event.getPlayer().getInventory().setItem(7, new SettingsItem());
                }
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Stopped")) {
                ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 10);
                ItemMeta meta = start_stop.getItemMeta();
                meta.setDisplayName("§aRunning §7(Right-Click to use)");
                start_stop.setItemMeta(meta);
                event.getPlayer().getInventory().setItem(4, start_stop);
                ReplayManager.runReplay(event.getPlayer());
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Running")) {
                ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
                ItemMeta meta = start_stop.getItemMeta();
                meta.setDisplayName("§cStopped §7(Right-Click to use)");
                start_stop.setItemMeta(meta);
                event.getPlayer().getInventory().setItem(4, start_stop);
                ReplayManager.pauseReplay(event.getPlayer());
            }
        }
    }

    private DecimalFormat getFormat() {
        return new DecimalFormat("0.000");
    }
}
