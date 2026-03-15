package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class spectate implements CommandExecutor {
    public static HashMap<Player, String> playersSpectating = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "§cYou can't use this command as console");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1 && playersSpectating.containsKey(player)) {

            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Main.getInstance().getConfigManager().getUsageMessage(CommandType.SPECTATE));
            return true;
        }
        Player player1 = Bukkit.getPlayer(args[0]);
        if (player1 == null) {
            player.sendMessage(Main.getInstance().PREFIX + "§cPlayer does not exist.");
            return true;
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player1);
        player.sendMessage(Main.getInstance().PREFIX + "Retype /spectate to exit spectate mode.");
        return true;
    }
}
