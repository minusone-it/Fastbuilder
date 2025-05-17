package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import net.luckperms.api.model.user.User;
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
        String prefix = "§7";
        if (Main.getInstance().getConfigManager().isLuckPermsSupportEnabled()) {
            User user = Main.getInstance().luckpermsApi.getUserManager().getUser(target.getUniqueId());
            prefix = String.valueOf(user.getCachedData().getMetaData().getPrefix().charAt(0)) + user.getCachedData().getMetaData().getPrefix().charAt(1);
        }
        if (!args[0].startsWith("#")) {
            player.sendMessage(prefix + args[0] + "§7's stats:");
            player.sendMessage("§7Personal Best: §e" + new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(target.getUniqueId(), Main.getInstance().getModeOfPlayer(player))).replace(",", "."));
            player.sendMessage("§7Total Attempts: §e" + Main.getInstance().getConfigManager().getAttempts(target));
            player.sendMessage("§7Successful Attempts: §e" + Main.getInstance().getConfigManager().getSuccessfulAttempts(target));
            player.sendMessage("§eCoins: " + Main.getInstance().getConfigManager().getCoinsOfPlayer(target));
        } else {
            OfflinePlayer targetA = Bukkit.getServer().getOfflinePlayer(Main.getInstance().getConfigManager().getTopPlayerInModeByIndex(Integer.parseInt(args[0].replace("#", "")) - 1, Main.getInstance().getModeOfPlayer(player)));
            player.sendMessage(prefix + targetA.getName() + "§7's stats:");
            player.sendMessage("§7Personal Best: §e" + new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(targetA.getUniqueId(), Main.getInstance().getModeOfPlayer(player))).replace(",", "."));
            player.sendMessage("§7Total Attempts: §e" + Main.getInstance().getConfigManager().getAttempts(targetA));
            player.sendMessage("§7Successful Attempts: §e" + Main.getInstance().getConfigManager().getSuccessfulAttempts(targetA));
            player.sendMessage("§eCoins: " + Main.getInstance().getConfigManager().getCoinsOfPlayer(targetA));
        }
        return true;
    }

}
