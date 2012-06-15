package me.adpuckey.plugins.aprilonirc.command;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.IrcMessage;
import me.adpuckey.plugins.aprilonirc.irc.IrcColor;
import me.adpuckey.plugins.aprilonirc.irc.MessageTypes;
import me.adpuckey.plugins.aprilonirc.irc.IrcUser;
import me.adpuckey.plugins.aprilonirc.utils.ChatManagerUtils;
import me.adpuckey.plugins.aprilonirc.utils.Utils;
import me.adpuckey.plugins.aprilonirc.utils.IrcInGamePrefixes;
import java.util.List;

import java.util.regex.Pattern;
import me.adpuckey.plugins.aprilonirc.IrcMessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IrcCommandHandler {
    private final AprilonIrc Parent;
    
    public IrcCommandHandler(AprilonIrc plugin){ Parent = plugin; }
    
    public boolean onCommand(String[] split, IrcMessage rawmessage)
    {
        if(split[0].equalsIgnoreCase(".chan")){ reply(rawmessage, ".chan has changed to \". \""); return true; }
        if(split[0].equalsIgnoreCase(".") || split[0].startsWith("..") || split[0].startsWith("._") || split[0].startsWith(".-")) return true;
        
        if(split[0].equalsIgnoreCase(".help"))
        {
            reply(rawmessage, "Commands: .help .list .msg .give .gamemode .kick .(set/del)(prefix/suffix) .stop");
            reply(rawmessage, "Start a sentence with \". \" for a message to be ignored by the irc bot.");
            return true;
        }
        
        if(split[0].equalsIgnoreCase(".list"))
        {
            Player[] onlineplayers = Parent.getServer().getOnlinePlayers();                                              
            if(onlineplayers.length == 0)
            {
                try { rawmessage.getClient().PrivMsg(rawmessage.getChannel(), "Nobody is minecrafting right now.", false); }
                catch(Exception e) {}
                
                return true;
            }  
            
            String playerstring = "";
                        
            for(int i = 0; i <= (onlineplayers.length - 1); i++)
            {
                if(i != 0) playerstring += ", ";                
                
                if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) playerstring += IrcColor.formatMCMessage(ChatManagerUtils.ParseDisplayName(onlineplayers[i]));
                else playerstring += onlineplayers[i].getDisplayName();                
            }
            String message_prefix = onlineplayers.length + "/" + Parent.getServer().getMaxPlayers() + " players online: ";         
            reply(rawmessage,  message_prefix + playerstring);            
            return true;
        }
        if(split[0].equalsIgnoreCase(".stop"))        
        {
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel."); 
            if(rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ Parent.getServer().shutdown(); return true; }
            else
            {
                reply(rawmessage, "You need to be ChanOp to do that command.");
                return true;
            }
        }
        if(split[0].equalsIgnoreCase(".gamemode"))
        {
            if(split.length < 3){ reply(rawmessage, "Usage:  .gamemode <player> <gamemode 1|0>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel."); 
            if(rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel()))
            {
                Player p = Utils.NarrowMatchPlayer(Parent.getServer(), split[1]);
                if(p == null){reply(rawmessage, "Couldn't find player \"" + split[1] + "\""); return true; }
                
                if(split[2].equals("1"))
                {
                    p.setGameMode(GameMode.CREATIVE);
                    reply(rawmessage, "Gave player " + p.getName() + " CREATIVE mode.");
                    return true;
                }
                if(split[2].equals("0"))
                {
                    p.setGameMode(GameMode.SURVIVAL);
                    reply(rawmessage, "Gave player " + p.getName() + " SURVIVAL mode.");
                    return true;
                }
                else { reply(rawmessage, "Usage:  .gamemode <player> <gamemode 1|0>"); return true; }
            }
            else
            {
                reply(rawmessage, "You need to be ChanOp to do that command.");
                return true;
            }
        }
        if(split[0].equalsIgnoreCase(".give"))
        {
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel."); 
            if(split.length < 4){ reply(rawmessage, "Usage: .give <player> <item id> <amount>"); return true; }
            if(rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel()))
            {
                Player p = Utils.NarrowMatchPlayer(Parent.getServer(), split[1]);
                if(p == null){reply(rawmessage, "Couldn't find player \"" + split[1] + "\""); return true; }
                
                int id;
                int amount;
                
                try
                {
                    id = Integer.parseInt(split[2]);
                    amount = Integer.parseInt(split[3]);
                }
                catch(NumberFormatException e){ reply(rawmessage, "Usage: .give <player> <item id> <amount>"); return true; }
                
                Material material = Material.getMaterial(id);
                if(material == null){ reply(rawmessage, "Couldn't match a material for item id " + id + "."); return true; }
                
                p.getInventory().addItem(new ItemStack(material, amount));
                p.sendMessage(ChatColor.GREEN + "IRC user " + rawmessage.getUser().getNick() + " gave you " + amount + " of " + material.toString() + "!");
                reply(rawmessage, "Gave player " + p.getName() + " " + amount + " of " + material.toString() + ".");
                return true;
            }
            else
            {
                reply(rawmessage, "You need to be ChanOp to do that command.");
                return true;
            }
        }
        if(split[0].equalsIgnoreCase(".msg"))
        {
            if(split.length < 3) { reply(rawmessage, "Usage:  .msg <player name> <message>"); return true; }
                
            Player p = Utils.NarrowMatchPlayer(Parent.getServer(), split[1]);
            String message = Utils.ArrayToString(split, 2, 0);
            
            if(p == null){ reply(rawmessage, "Couldn't find player \"" + split[1] + "\""); return true; }
            p.sendMessage("[" + ChatColor.GRAY + "IRC PM From " + rawmessage.getUser().getNick() + ChatColor.WHITE + "] " + message);
            return true;
        }
        if(split[0].equalsIgnoreCase(".setprefix"))
        {
            IrcMessageHandler.ClearCheckedHostmasks();
            if(split.length < 3){ reply(rawmessage, "Usage:  .setprefix <user> <prefix>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel.");
            if(!rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ reply(rawmessage, "You must have ChanOp to do that command."); return true; }
            
            String source = split[1];
            if(!source.contains("."))
            {
                IrcUser u = rawmessage.getClient().getUser(split[1]);
                if(u != null) source = u.getSource();
            }            
                        
            if(IrcInGamePrefixes.WritePrefix(source, split[2]) == true){ reply(rawmessage, "Successfully set prefix of " + source + " ."); return true; }
            reply(rawmessage, "Error setting prefix.");
            return true;
        }
        if(split[0].equalsIgnoreCase(".setsuffix"))
        {
            IrcMessageHandler.ClearCheckedHostmasks();
            if(split.length < 3){ reply(rawmessage, "Usage:  .setsuffix <user> <prefix>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel.");
            if(!rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ reply(rawmessage, "You must have ChanOp to do that command."); return true; }
            
            String source = split[1];
            if(!source.contains("."))
            {
                IrcUser u = rawmessage.getClient().getUser(split[1]);
                if(u != null) source = u.getSource();
            }
            
            if(IrcInGamePrefixes.WriteSuffix(source, split[2]) == true){ reply(rawmessage, "Successfully set suffix of " + source + " ."); return true; }
            reply(rawmessage, "Error setting suffix.");
            return true;
        }
        if(split[0].equalsIgnoreCase(".delprefix"))
        {
            IrcMessageHandler.ClearCheckedHostmasks();
            if(split.length < 2){ reply(rawmessage, "Usage:  .delprefix <user>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel.");
            if(!rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ reply(rawmessage, "You must have ChanOp to do that command."); return true; }
            
            String source = split[1];
            if(!source.contains("."))
            {
                IrcUser u = rawmessage.getClient().getUser(split[1]);
                if(u != null) source = u.getSource();
            }
            
            if(IrcInGamePrefixes.DeletePrefix(source) == true){ reply(rawmessage, "Successfully deleted prefix of " + source + " ."); return true; }
            reply(rawmessage, "Error deleting prefix.");
            return true;
        }
        if(split[0].equalsIgnoreCase(".delsuffix"))
        {
            IrcMessageHandler.ClearCheckedHostmasks();
            if(split.length < 2){ reply(rawmessage, "Usage:  .delsuffix <user>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel.");
            if(!rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ reply(rawmessage, "You must have ChanOp to do that command."); return true; }
            
            String source = split[1];
            if(!source.contains("."))
            {
                IrcUser u = rawmessage.getClient().getUser(split[1]);
                if(u != null) source = u.getSource();
            }
            
            if(IrcInGamePrefixes.DeleteSuffix(source) == true){ reply(rawmessage, "Successfully deleted suffix of " + source + " ."); return true; }
            reply(rawmessage, "Error deleting suffix.");
            return true;
        }
        if(split[0].equalsIgnoreCase(".kick"))
        {
            if(split.length < 2){ reply(rawmessage, "Usage:  .kick <user>"); return true; }
            if(rawmessage.getType() != MessageTypes.CHANMSG) reply(rawmessage, "You must do this command in an IRC channel.");
            if(!rawmessage.getUser().getOpChannels().contains(rawmessage.getChannel())){ reply(rawmessage, "You must have ChanOp to do that command."); return true; }
            
            String kickmsg = "Kicked by IRC Operator.";
            if(split.length > 2) kickmsg = Utils.ArrayToString(split, 2, 0);
            
            List<Player> list = Parent.getServer().matchPlayer(split[1]);
            if(list.isEmpty())
            {
                reply(rawmessage, "No players matched for \"" + split[1] + "\".");
                return true;
            }
            for(Player p : list)
            {
                p.kickPlayer(kickmsg);
                for(Player pl : Parent.getServer().getOnlinePlayers()) pl.sendMessage(ChatColor.YELLOW + p.getName() + " kicked by IRC Operator (\"" + kickmsg + "\").");
            }
            
            reply(rawmessage, "Kicked player(s).");
            return true;
        }
        
        return false;
    }
    
    private void reply(IrcMessage rawmessage, String message)
    {
        if(rawmessage.getType() == MessageTypes.CHANMSG)
        {
            try{ rawmessage.getClient().PrivMsg(rawmessage.getChannel(), message, false); }
            catch(Exception e){}
            return;
        }
        else if(rawmessage.getType() == MessageTypes.QUERYMSG)
        {
            try{ rawmessage.getClient().PrivMsg(rawmessage.getUser().getNick(), message, false); }
            catch(Exception e) {}
            return;
        }
    }
}
