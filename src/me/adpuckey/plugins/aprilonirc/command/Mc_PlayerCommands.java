package me.adpuckey.plugins.aprilonirc.command;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class Mc_PlayerCommands implements CommandExecutor{
    AprilonIrc Parent;

    public Mc_PlayerCommands(AprilonIrc plugin){ Parent = plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("irc"))
        {
            if(args[0].equalsIgnoreCase("help"))
            {
                sender.sendMessage(ChatColor.GREEN + "AprilonIrc by HAPPYGOPUCKEY");
                sender.sendMessage(ChatColor.YELLOW + "/irc pm <user> <message>" + ChatColor.WHITE + ": Sends a PM to a user on IRC.");
                return true;
            }
            if(args[0].equalsIgnoreCase("pm"))
            {
                if(args.length < 3)
                {
                    sender.sendMessage(ChatColor.RED + "Usage:  /irc pm <user> <message>");
                    return true;
                }
                
                String message = Utils.ArrayToString(args, 2, 0);
                try
                {
                    if(sender instanceof Player)
                    {
                        Player p = (Player) sender;
                        Parent.Manager.getCurrentClient().PrivMsg(args[1], "Minecraft message from " + p.getName() + ": " + message, true); 
                    }
                    else Parent.Manager.getCurrentClient().PrivMsg(args[1], "Minecraft message from console: " + message, true); 
                }
                catch(Exception e)
                {
                    Utils.LogException(e, "sending IRC query message");
                    sender.sendMessage(ChatColor.RED + "Unable to send IRC message.  Please contact an administrator.");
                }
                return true;                
            }
        }
        
        sender.sendMessage(ChatColor.RED + "Use /irc help for a list of commands.");
        return true;
    }
}
