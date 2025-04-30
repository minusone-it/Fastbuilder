package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class stats implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "Usage: /stats <player>");
            return false;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getServer().getOfflinePlayer(args[0]).getPlayer();
        player.sendMessage(Main.getInstance().PREFIX + args[0] + "'s stats:");
        player.sendMessage(Main.getInstance().PREFIX + "     §eCoins: " + Main.getInstance().getConfigManager().getCoinsOfPlayer(target));
        player.sendMessage(Main.getInstance().PREFIX + "     §7Total Attempts: " + Main.getInstance().getConfigManager().getAttempts(target));
        player.sendMessage(Main.getInstance().PREFIX + "     §7Successful Attempts: " + Main.getInstance().getConfigManager().getSuccessfulAttempts(target));
        player.sendMessage(Main.getInstance().PREFIX + "     §aPersonal Best: " + Main.getInstance().getConfigManager().getPB(target.getUniqueId(), Main.getInstance().getModeOfPlayer(player)));
        return true;
    }

}
