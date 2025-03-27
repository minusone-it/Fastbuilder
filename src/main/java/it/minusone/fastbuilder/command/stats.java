package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
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
        Player target = Main.getInstance().getServer().getPlayer(args[0]);
        player.sendMessage(Main.getInstance().PREFIX + target.getName() + "'s stats:");
        player.sendMessage(Main.getInstance().PREFIX + "     §eCoins: " + Main.getInstance().getConfigManager().getCoinsOfPlayer(target));
        player.sendMessage(Main.getInstance().PREFIX + "     §aPersonal Bests:");
        for (String mode : Main.getInstance().getConfigManager().getModes()) {
            player.sendMessage(Main.getInstance().PREFIX + "     " + mode + ": " + new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(player.getUniqueId(), mode)).replace(",", "."));
        }
        return true;
    }
}
