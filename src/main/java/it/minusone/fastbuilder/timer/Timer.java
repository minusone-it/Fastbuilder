package it.minusone.fastbuilder.timer;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.LobbyItem;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Timer {

    private final HashMap<UUID, Integer> playerTimers = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> runningTasks = new HashMap<>();
    private final HashMap<UUID, Boolean> alreadyTeleported = new HashMap<>();

    public void startTimer(Player player) {

        UUID uuid = player.getUniqueId();
        setAlreadyTeleported(player, false);

        if (runningTasks.containsKey(uuid)) {
            runningTasks.get(uuid).cancel();
            runningTasks.remove(uuid);
            playerTimers.remove(uuid);
        }

        playerTimers.put(uuid, 0);
        BukkitRunnable task = getBukkitRunnable(player, uuid);
        if (Main.getInstance().isPaper())
            task.runTaskTimerAsynchronously(Main.getInstance(), 0L, 1L);
        else
            task.runTaskTimerAsynchronously(Main.getInstance(), 0L, 0L);
        runningTasks.put(uuid, task);
        Replay replay = new Replay(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]));
        replay.addPlayer(player);
        replay.addRecording();
        replay.start();
        Main.getInstance().replays.put(uuid, replay);
        Main.getInstance().lastReplay.put(uuid, replay);
    }

    private BukkitRunnable getBukkitRunnable(Player player, UUID uuid) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerTimers.containsKey(uuid)) {
                    if (runningTasks.containsKey(uuid)) {
                        BukkitRunnable task = runningTasks.get(uuid);
                        if (task != null && task.getTaskId() != -1) {
                            task.cancel();
                            return;
                        }
                    }
                    runningTasks.remove(uuid);
                    return;
                }

                playerTimers.put(uuid, playerTimers.get(uuid) + 1);

                double bestTime = Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);
                Main.getInstance().scoreboardManager.updateScoreboard(player, getTimeFormatted(player).replace(",", "."), bestTime);
                if (Main.getInstance().getConfigManager().isActionBarEnabled()) {
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(Main.getInstance().getConfigManager().getActionBarMessage(player)), (byte) 2));
                }
            }
        };
    }

    public void stopTimer(Player player, boolean saveReplay) {
        UUID uuid = player.getUniqueId();
        playerTimers.remove(uuid);
        if (runningTasks.containsKey(uuid)) {
            runningTasks.get(uuid).cancel();
            runningTasks.remove(uuid);
        }
        Replay replay = Main.getInstance().replays.get(uuid);
        if (replay != null) {
            replay.removeRecording();
            replay.end();
        }
        if (!isAlreadyTeleported(player)) {
            if (saveReplay) {
                try {
                    File replayFolder = new File(Main.getInstance().getDataFolder() + File.separator + "replays" + File.separator + player.getUniqueId() + File.separator + Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);
                    if (!replayFolder.exists()) replayFolder.mkdirs();
                    File replayFile = new File(replayFolder.toPath() + File.separator + System.currentTimeMillis() + ".replay");
                    ReplayOutputStream replayOutputStream = new ReplayOutputStream(Files.newOutputStream(replayFile.toPath()));
                    assert replay != null;
                    replayOutputStream.writeReplay(replay);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred while saving the replay!");
                }
            }
        }
        Main.getInstance().replays.remove(uuid, replay);
        setAlreadyTeleported(player, false);
    }

    public String getTimeFormatted(Player player) {
        return new DecimalFormat("0.000").format(getTime(player)).replace(",", ".");
    }

    public double getTime(Player player) {
        return playerTimers.getOrDefault(player.getUniqueId(), 0) * 0.05;
    }

    public boolean isPlayerInTimer(Player player) {
        return runningTasks.containsKey(player.getUniqueId());
    }

    public boolean isAlreadyTeleported(Player player) {
        return alreadyTeleported.getOrDefault(player.getUniqueId(), false);
    }

    public void setAlreadyTeleported(Player player, boolean state) {
        alreadyTeleported.put(player.getUniqueId(), state);
    }

    public boolean scoreTime(Player player, boolean practiceBlocks) {
        String timeFormatted = Main.getInstance().timer.getTimeFormatted(player);
        double time = Main.getInstance().timer.getTime(player);
        List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
        if (!timeFormatted.equalsIgnoreCase("0.000") && !timeFormatted.equalsIgnoreCase("-0.050")) {
            Main.getInstance().timer.stopTimer(player, !practiceBlocks);
            double PB = Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().getModeOfPlayer(player));
            boolean equalTime = PB == time;
            boolean newPB = time < PB;
            int baseCoins = (int) (1000 / (time + 1));
            int efficiencyBonus = (int) ((double) playerBlocks.size() / (time + 1) * 10);
            int pbBonus = newPB ? 7 : 0;
            int coins = Math.max(10, (baseCoins + efficiencyBonus + pbBonus) / 10);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
            if (newPB) {
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

            List<String> timeMessages = Main.getInstance().getConfigManager().getTimeMessage();
            List<String> pbMessages = Main.getInstance().getConfigManager().getPersonalBestMessage();

            DecimalFormat format = this.getFormat();

            if (newPB || Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]) == 0.000) {
                for (String msg : pbMessages)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg
                            .replace("%pb%", format.format(PB))
                            .replace("%difference%", (equalTime ? "+" : "") + format.format(time - PB))
                            .replace("%time%", timeFormatted)
                    ));
            } else {
                for (String msg : timeMessages)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg
                            .replace("%pb%", format.format(PB))
                            .replace("%difference%", "+" + format.format(time - PB))
                            .replace("%time%", timeFormatted)
                    ));
            }
            if (!Main.getInstance().playersInPracticeMode.get(player.getUniqueId())) {
                sendTitle(player, Main.getInstance().getConfigManager().getScoreTitle().replace("%time%", timeFormatted).replace("%coins%", "" + coins).replace("&", "§"), Main.getInstance().getConfigManager().getScoreSubTitle().replace("%time%", timeFormatted).replace("%coins%", "" + coins).replace("&", "§"), 0, 70, 20);
                Main.getInstance().getConfigManager().addCoinsToPlayer(player, coins);
                Main.getInstance().getConfigManager().saveBestTime(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], time);
                Main.getInstance().updateLeaderboard(player, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], time);
            } else
                sendTitle(player, Main.getInstance().getConfigManager().getScoreTitle().replace("%time%", timeFormatted).replace("%coins%", "" + coins).replace("&", "§"), "", 0, 70, 20);
            player.getInventory().clear();
            Main.getInstance().scoreboardManager.updateScoreboard(player, timeFormatted, Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().getModeOfPlayer(player)));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Main.getInstance().mapWherePlayerIn.containsKey(p)) {
                    Main.getInstance().scoreboardManager.updateScoreboard(p);
                }
            }

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                    if (!isAlreadyTeleported(player))
                        player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]));
                    ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
                    block.setAmount(64);
                    player.getInventory().setItem(0, block);
                    player.getInventory().setItem(1, block);
                    player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                    if (Main.getInstance().playersInPracticeMode.get(player.getUniqueId()))
                        player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64));
                    player.getInventory().setItem(4, new IslandSelectorItem());
                    player.getInventory().setItem(6, new ReplayItem());
                    player.getInventory().setItem(7, new SettingsItem());
                    if (Main.getInstance().getConfigManager().isBungeeModeEnabled())
                        player.getInventory().setItem(8, new LobbyItem());
                }
            }, 40L);

            for (Location blockLocation : playerBlocks)
                blockLocation.getBlock().setType(Material.AIR);
            Main.getInstance().placedBlocks.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subtitleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");

        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON);
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
        PacketPlayOutTitle packetTimes = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(packetTimes);
        cp.getHandle().playerConnection.sendPacket(packetTitle);
        cp.getHandle().playerConnection.sendPacket(packetSubtitle);
    }

    private DecimalFormat getFormat() {
        return new DecimalFormat("0.000");
    }

}
