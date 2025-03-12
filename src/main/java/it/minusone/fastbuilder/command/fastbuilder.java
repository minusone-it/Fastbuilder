package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.SettingsItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class fastbuilder implements CommandExecutor {
    private final ConfigManager configManager;

    public fastbuilder() {
        this.configManager = new ConfigManager(Main.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "§cYou must be a player to execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Main.getInstance().PREFIX + "This server is running Fastbuilder by MinusOne.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addmode":
            case "removemode":
            case "set":
                if (!player.hasPermission("fastbuilder.admin")) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                break;
        }

        switch (args[0].toLowerCase()) {
            case "addmode":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder addmode <mode>");
                    return true;
                }
                String modeToAdd = args[1];
                configManager.addMode(modeToAdd);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Mode added: " + modeToAdd);
                break;

            case "removemode":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder removemode <mode>");
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

            case "set":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder set <mode> <island>");
                    return true;
                }

                String mode = args[1];
                String island = args[2];

                if (!configManager.modeExists(mode)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist! Use /fastbuilder addmode <mode> first.");
                    return true;
                }

                Location spawnLocation = player.getLocation();
                Main.getInstance().getConfig().set("spawns." + mode + "." + island, spawnLocation.serialize());
                Main.getInstance().saveConfig();

                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Spawn set for " + mode + " - " + island);
                Main.getInstance().configManager = null;
                Main.getInstance().configManager = new ConfigManager(Main.getInstance());
                break;

            case "join":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder join <mode> <island>");
                    return true;
                }

                String joinMode = args[1];
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

                if (Main.getInstance().modesWherePlayerIn.containsValue(joinMode) && Main.getInstance().islandWherePlayerIn.containsValue(joinIsland)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island is already occupied by " + Objects.requireNonNull(Main.getInstance().islandWherePlayerIn.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(joinIsland))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null)).getName() + "!");
                    return true;
                }

                player.teleport(joinLocation);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Joined " + joinMode + "-" + joinIsland + "!");
                player.getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
                player.getInventory().setItem(1, new ItemStack(Material.SANDSTONE, 64));
                player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                player.getInventory().setItem(4, new IslandSelector());
                //player.getInventory().setItem(6, new ReplayItem());
                player.getInventory().setItem(7, new SettingsItem());
                Main.getInstance().modesWherePlayerIn.put(player, joinMode);
                Main.getInstance().islandWherePlayerIn.put(player, joinIsland);
                Main.getInstance().scoreboardManager.updateTimer(player, "0.000", Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + joinMode, 0.000));
                break;

            default:
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Unknown command! Use /fastbuilder <addmode|removemode|set|join>");
                break;
        }
        return true;
    }
}
