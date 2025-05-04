package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class stats implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "Usage: /stats <player>");
            return false;
        }

        Player player = (Player) sender;
        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
        System.out.println(target.getUniqueId().toString());
        player.sendMessage(args[0] + "'s stats:");
        player.sendMessage("§eCoins: " + Main.getInstance().getConfigManager().getCoinsOfPlayer(target));
        player.sendMessage("§7Total Attempts: §e" + Main.getInstance().getConfigManager().getAttempts(target));
        player.sendMessage("§7Successful Attempts: §e" + Main.getInstance().getConfigManager().getSuccessfulAttempts(target));
        player.sendMessage("§7Personal Best: §e" + new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(target.getUniqueId(), Main.getInstance().getModeOfPlayer(player))).replace(",", "."));
        return true;
    }

}
