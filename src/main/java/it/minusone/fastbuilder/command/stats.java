package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class stats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2 || !(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "Usage: /stats <player> <mode>");
            return false;
        }

        Player player = (Player) sender;
        if (Main.getInstance().getConfigManager().modeExists(args[1])) {
            player.sendMessage(Main.getInstance().PREFIX + "Personal Best for " + args[0] + " in mode " + args[1] + ":" + Main.getInstance().getConfigManager().getPB(player.getUniqueId(), args[1]));
        } else
            player.sendMessage(Main.getInstance().PREFIX + "Mode " + args[1] + " doesn't exist!");
        return true;
    }
}
