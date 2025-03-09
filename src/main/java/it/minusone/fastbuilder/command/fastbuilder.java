package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class fastbuilder implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0)
                player.sendMessage(Main.getInstance().PREFIX + "This server is running Fastbuilder by MinusOne.");
            else if (args.length == 1) {

            }
        } else
            sender.sendMessage(Main.getInstance().PREFIX + "§cYou must be a player to execute this command.");
        return false;
    }
}
