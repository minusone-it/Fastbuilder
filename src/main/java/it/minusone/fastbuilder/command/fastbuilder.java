package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.CommandType;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.LobbyItem;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.npc.NPC;
import it.minusone.fastbuilder.npc.NPCManager;
import it.minusone.fastbuilder.replay.ReplayManager;
import it.minusone.fastbuilder.utils.bukkit.entity.HologramStack;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class fastbuilder implements CommandExecutor {
    private final ConfigManager configManager = Main.getInstance().getConfigManager();

    public static void teleportPlayerToIslandB(Player player, String mode, String island) {
        Player islandPlayer = Main.getInstance().getPlayerInMode(mode + "-" + island);
        if (islandPlayer != null) {
            player.sendMessage(Main.getInstance().getConfigManager().getIslandOccupiedMessage(player, island));
            return;
        }
        leaveModeB(player);
        Location location = Main.getInstance().getConfigManager().getSpawnLocation(mode, island);
        player.teleport(location);
        ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
        block.setAmount(64);
        player.getInventory().setItem(0, block);
        player.getInventory().setItem(1, block);
        player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
        if (Main.getInstance().playersInPracticeMode.get(player.getUniqueId())) {
            ItemStack item = new ItemStack(Material.STAINED_CLAY, 64);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§rPractice Blocks");
            item.setItemMeta(meta);
            player.getInventory().setItem(3, item);
        }
        player.getInventory().setItem(4, new IslandSelectorItem());
        player.getInventory().setItem(6, new ReplayItem());
        player.getInventory().setItem(7, new SettingsItem());
        if (Main.getInstance().getConfigManager().isBungeeModeEnabled())
            player.getInventory().setItem(8, new LobbyItem());
        Main.getInstance().mapWherePlayerIn.put(player, mode + "-" + island);
        if (Main.getInstance().getConfigManager().npcsToggled()) {
            Location baseLocationA = location.clone();
            Location offsetA = Main.getInstance().getConfigManager().getNPCOffset(mode);
            baseLocationA.setWorld(player.getWorld());
            baseLocationA.add(offsetA.getX(), offsetA.getY(), offsetA.getZ());
            NPC modeSelector = new NPC(player, Main.getInstance().getConfigManager().getNPCname(), baseLocationA);
            if (NPCManager.getNPC(player) != null)
                NPCManager.removeNPC(NPCManager.getNPC(player), player);

            modeSelector.spawn(player);
            NPCManager.setNPC(modeSelector, player);
        }
        if (Main.getInstance().getConfigManager().holosToggled()) {
            Location baseLocationB = location.clone();
            Location offsetB = Main.getInstance().getConfigManager().getHoloOffset(mode, player.getLocation().getWorld());
            baseLocationB.setWorld(player.getWorld());
            baseLocationB.add(offsetB.getX(), offsetB.getY(), offsetB.getZ());
            HologramStack stats = new HologramStack(
                    Main.getInstance().getConfigManager().getHoloLines(player).length,
                    0.3,
                    true,
                    false,
                    Main.getInstance().getConfigManager().getHoloLines(player)
            );
            stats.setLocation(baseLocationB);
            stats.spawn();
            Main.getInstance().stats.put(player, stats);
        }
        Main.getInstance().scoreboardManager.updateScoreboard(player, "0.000", Main.getInstance().getConfigManager().getPB(player.getUniqueId(), mode));
        Main.getInstance().scoreboardManager.updateScoreboard(player);
    }

    public static void teleportPlayerToIsland(Player player, String mode, String island) {
        Player islandPlayer = Main.getInstance().getPlayerInMode(mode + "-" + island);
        if (islandPlayer != null) {
            player.sendMessage(Main.getInstance().getConfigManager().getIslandOccupiedMessage(player, island));
            return;
        }
        leaveMode(player);
        List<Location> playerBlocks = new ArrayList<>();
        if (Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()) != null)
            playerBlocks.addAll(Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()));
        for (Location blockLocation : playerBlocks) {
            blockLocation.getBlock().setType(Material.AIR);
        }
        Main.getInstance().getConfigManager().addAttempt(player);
        Main.getInstance().placedBlocks.remove(player.getUniqueId());
        Location location = Main.getInstance().getConfigManager().getSpawnLocation(mode, island);
        player.teleport(location);
        player.sendMessage(Main.getInstance().getConfigManager().getJoinMessage());
        ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
        block.setAmount(64);
        player.getInventory().setItem(0, block);
        player.getInventory().setItem(1, block);
        player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
        player.getInventory().setItem(4, new IslandSelectorItem());
        player.getInventory().setItem(6, new ReplayItem());
        player.getInventory().setItem(7, new SettingsItem());
        if (Main.getInstance().getConfigManager().isBungeeModeEnabled())
            player.getInventory().setItem(8, new LobbyItem());
        Main.getInstance().mapWherePlayerIn.put(player, mode + "-" + island);
        Location baseLocationA = location.clone();
        Location offsetA = Main.getInstance().getConfigManager().getNPCOffset(mode);
        baseLocationA.setWorld(player.getWorld());
        baseLocationA.add(offsetA.getX(), offsetA.getY(), offsetA.getZ());
        NPC modeSelector = new NPC(player, Main.getInstance().getConfigManager().getNPCname(), baseLocationA);
        if (NPCManager.getNPC(player) != null)
            NPCManager.removeNPC(NPCManager.getNPC(player), player);
        modeSelector.spawn(player);
        NPCManager.setNPC(modeSelector, player);
        Location baseLocationB = location.clone();
        Location offsetB = Main.getInstance().getConfigManager().getHoloOffset(mode, player.getLocation().getWorld());
        baseLocationB.setWorld(player.getWorld());
        baseLocationB.add(offsetB.getX(), offsetB.getY(), offsetB.getZ());
        HologramStack stats = new HologramStack(
                Main.getInstance().getConfigManager().getHoloLines(player).length,
                0.3,
                true,
                false,
                Main.getInstance().getConfigManager().getHoloLines(player)
        );
        if (Main.getInstance().stats.containsKey(player)) {
            Main.getInstance().stats.get(player).despawn();
            Main.getInstance().stats.get(player).delete();
            Main.getInstance().stats.remove(player);
        }
        stats.setLocation(baseLocationB);
        stats.spawn();
        Main.getInstance().stats.put(player, stats);
        Main.getInstance().scoreboardManager.updateScoreboard(player, "0.000", Main.getInstance().getConfigManager().getPB(player.getUniqueId(), mode));
        Main.getInstance().scoreboardManager.updateScoreboard(player);
    }

    private static void leaveMode(Player player) {
        if (Main.getInstance().mapWherePlayerIn.get(player) != null) {
            String currentMode = Main.getInstance().mapWherePlayerIn.get(player).split("-")[0];
            String currentIsland = Main.getInstance().mapWherePlayerIn.get(player).split("-")[1];
            if (currentMode != null && currentIsland != null) {
                Main.getInstance().timer.stopTimer(player, false);
                Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);
                Main.getInstance().mapWherePlayerIn.remove(player);
                if (NPCManager.getNPC(player) != null) {
                    NPCManager.removeNPC(NPCManager.getNPC(player), player);
                }

                List<Location> playerBlocks = new ArrayList<>();
                if (Main.getInstance().placedBlocks.get(player.getUniqueId()) != null)
                    playerBlocks.addAll(Main.getInstance().placedBlocks.get(player.getUniqueId()));
                if (Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()) != null)
                    playerBlocks.addAll(Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()));
                for (Location blockLocation : playerBlocks) {
                    blockLocation.getBlock().setType(Material.AIR);
                }
                Main.getInstance().getConfigManager().addAttempt(player);
                Main.getInstance().placedBlocks.remove(player.getUniqueId());

                player.getInventory().clear();
                Location spawn = Main.getInstance().getConfigManager().getSpawnLocation(currentMode, currentIsland);
                if (spawn != null) {
                    player.teleport(spawn);
                }
                Main.getInstance().stats.get(player).despawn();
                Main.getInstance().stats.get(player).delete();
                Main.getInstance().stats.remove(player);
                for (Player p : Bukkit.getOnlinePlayers())
                    Main.getInstance().scoreboardManager.updateScoreboard(p);
            }
        }
    }

    private static void leaveModeB(Player player) {
        if (Main.getInstance().mapWherePlayerIn.get(player) != null) {
            String currentMode = Main.getInstance().mapWherePlayerIn.get(player).split("-")[0];
            String currentIsland = Main.getInstance().mapWherePlayerIn.get(player).split("-")[1];
            if (currentMode != null && currentIsland != null) {
                Main.getInstance().timer.stopTimer(player, false);
                Main.getInstance().mapWherePlayerIn.remove(player);
                if (NPCManager.getNPC(player) != null) {
                    NPCManager.removeNPC(NPCManager.getNPC(player), player);
                }

                List<Location> playerBlocks = new ArrayList<>();
                if (Main.getInstance().placedBlocks.get(player.getUniqueId()) != null)
                    playerBlocks.addAll(Main.getInstance().placedBlocks.get(player.getUniqueId()));
                if (Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()) != null)
                    playerBlocks.addAll(Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()));
                for (Location blockLocation : playerBlocks) {
                    blockLocation.getBlock().setType(Material.AIR);
                }
                Main.getInstance().getConfigManager().addAttempt(player);
                Main.getInstance().placedBlocks.remove(player.getUniqueId());

                player.getInventory().clear();
                Location spawn = Main.getInstance().getConfigManager().getSpawnLocation(currentMode, currentIsland);
                if (spawn != null) {
                    player.teleport(spawn);
                }

                Main.getInstance().stats.get(player).despawn();
                Main.getInstance().stats.get(player).delete();
                Main.getInstance().stats.remove(player);
            }
        }
    }

    public static String findClosestIslandInMode(String mode) {
        for (String island : Main.getInstance().getConfigManager().getIslandsOfMod(mode)) {
            if (Main.getInstance().mapWherePlayerIn.containsValue(mode + "-" + island)) {
                continue; // Skip islands that are already occupied
            }

            return island; // Return the first available island in the list
        }

        return null; // No available island found
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "§cYou must be a player to execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Main.getInstance().PREFIX + "§fRunning Fastbuilder V" + Main.getInstance().getDescription().getVersion() + " by _MinusOne_.");
            player.sendMessage(Main.getInstance().PREFIX + "§fBuy it on https://builtbybit.com/resources/fastbuilder.62493/.");
            if (player.hasPermission("fastbuilder.admin")) {
                player.sendMessage(Main.getInstance().PREFIX + "List of commands:");
                player.sendMessage("§7/fb join <mode> <island_number> - To join a mode.");
                player.sendMessage("§7/fb addmode <name> <isInclined(true | false)> - To add a mode.");
                player.sendMessage("§7/fb removemode <mode> - To remove a mode.");
                player.sendMessage("§7/fb addisland <mode>- To set spawn of an island of a mode.");
                player.sendMessage("§7/fb set <mode> <island_number> <isInclined> - To set spawn of an island of a mode.");
                player.sendMessage("§7/fb setcoins <player> <amount> - To set coins of a player.");
                player.sendMessage("§7/fb addcoins <player> <amount> - To add coins to a player.");
                player.sendMessage("§7/fb wipestats <player> - To wipe the stats of a player.");
                player.sendMessage("§7/fb watchreplay <player> <mode> <replay_id> - To see the replay of a player.");
                player.sendMessage("§7/fb reloadconfig - To reload plugin config.");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addcoins":
            case "setcoins":
            case "addmode":
            case "removemode":
            case "set":
            case "wipestats":
            case "reloadconfig":
            case "addisland":
            case "watchreplay":
                if (!player.hasPermission("fastbuilder.admin")) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                break;
        }

        switch (args[0].toLowerCase()) {
            case "watchreplay":
                if (args.length < 4) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.SEEREPLAY));
                    return true;
                }
                File replayFolder = new File(Main.getPlugin(Main.class).getDataFolder(),
                        "replays" + File.separator + Bukkit.getOfflinePlayer(args[1]).getUniqueId() + File.separator + args[2] + File.separator);
                if (replayFolder.exists()) {
                    List<File> replayFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(replayFolder.listFiles())));
                    for (File file : replayFiles) {
                        if (Paths.get(file.getPath()).getFileName().toString().replace(".replay", "").equalsIgnoreCase(args[3])) {
                            if (!Objects.equals(Main.getInstance().getModeOfPlayer(player), args[3]))
                                player.performCommand("/fb join " + args[3]);
                            player.getInventory().clear();
                            ReplayManager.playReplay(player, file);
                            break;
                        }
                    }
                    player.sendMessage(Main.getInstance().PREFIX + "§cReplay not found.");
                } else {
                    player.sendMessage(Main.getInstance().PREFIX + "§cReplay not found.");
                }
                break;
            case "addcoins":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.ADDCOINS));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                Main.getInstance().getConfigManager().addCoinsToPlayer(target, Integer.parseInt(args[2]));
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Added " + args[2] + " coins to " + args[1] + ".");
                break;

            case "setcoins":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.SETCOINS));
                    return true;
                }
                OfflinePlayer targetA = Bukkit.getOfflinePlayer(args[1]);
                if (targetA == null) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Player not found!");
                    return true;
                }
                Main.getInstance().getConfigManager().setCoinsOfPlayer(targetA, Integer.parseInt(args[2]));
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Set " + args[2] + " coins to " + args[1] + ".");
                break;

            case "addmode":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.ADDMODE));
                    return true;
                }
                String modeToAdd = args[1];
                configManager.addMode(modeToAdd, Boolean.parseBoolean(args[2]));
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Mode added: " + modeToAdd);
                break;

            case "removemode":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.REMOVEMODE));
                    return true;
                }
                String modeToRemove = args[1];
                if (!configManager.modeExists(modeToRemove)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                    return true;
                }
                configManager.removeMode(modeToRemove);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Mode removed: " + modeToRemove);
                break;
            case "addisland":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.ADDISLAND));
                    return true;
                }
                String modeA = args[1];
                if (!configManager.modeExists(modeA)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist! Use /fastbuilder addmode <mode> <isInclined(true|false)> first.");
                    return true;
                }
                Location spawnLocationA = player.getLocation();
                int islandA = Main.getInstance().getConfigManager().getIslandsOfMod(modeA).size() + 1;
                Main.getInstance().getConfigManager().setSpawnLocation(modeA, islandA + "", spawnLocationA);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Spawn set for " + modeA + "-" + islandA);
                break;
            case "set":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.SET));
                    return true;
                }

                String mode = args[1];
                String island = args[2];

                if (!configManager.modeExists(mode)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist! Use /fastbuilder addmode <mode> <isInclined(true | false)> first.");
                    return true;
                }

                Location spawnLocation = player.getLocation();
                Main.getInstance().getConfigManager().setSpawnLocation(mode, island, spawnLocation);

                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Spawn set for " + mode + "-" + island);
                break;
            case "reloadconfig":
                player.sendMessage(Main.getInstance().PREFIX + "Reloading config...");
                long milliseconds = System.currentTimeMillis();
                Main.getInstance().getConfigManager().reloadConfigs();
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Config reloaded in " + (System.currentTimeMillis() - milliseconds) + "ms!");
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.JOIN));
                    return true;
                }

                String joinMode = args[1];

                if (args.length < 3) {
                    if (!configManager.modeExists(joinMode)) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                        return true;
                    }

                    String closestIsland = findClosestIslandInMode(joinMode);
                    if (closestIsland == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "No islands found in this mode!");
                        return true;
                    }

                    Location joinLocation = configManager.getSpawnLocation(joinMode, closestIsland);
                    if (joinLocation == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island does not exist!");
                        return true;
                    }

                    if (Main.getInstance().mapWherePlayerIn.get(player) != null) {

                        Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);

                        leaveMode(player);
                    }

                    teleportPlayerToIsland(player, joinMode, closestIsland);
                } else {
                    String joinIsland = args[2];

                    if (!configManager.modeExists(joinMode)) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                        return true;
                    }

                    Location joinLocation = configManager.getSpawnLocation(joinMode, joinIsland);
                    if (joinLocation == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island does not exist!");
                        return true;
                    }

                    if (Main.getInstance().mapWherePlayerIn.get(player) != null) {

                        Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);

                        leaveMode(player);
                    }
                    teleportPlayerToIsland(player, joinMode, joinIsland);
                }
                break;
            case "wipestats":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.WIPESTATS));
                    return true;
                }
                OfflinePlayer targetB = Bukkit.getOfflinePlayer(args[1]);
                Main.getInstance().getConfigManager().deleteStatsOfPlayer(targetB);
                player.sendMessage(Main.getInstance().PREFIX + "§2Successfully wiped " + targetB.getName() + "'s stats!");
                break;
            default:
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Unknown command! Use /fastbuilder to see list of commands.");
                break;
        }
        return true;
    }

}
