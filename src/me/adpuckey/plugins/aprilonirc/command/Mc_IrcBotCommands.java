package me.adpuckey.plugins.aprilonirc.command;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.IrcClient;
import me.adpuckey.plugins.aprilonirc.utils.Utils;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Mc_IrcBotCommands implements CommandExecutor{
    private final AprilonIrc Parent;
    
    public Mc_IrcBotCommands(AprilonIrc plugin){ Parent = plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(label.equalsIgnoreCase("ircbot"))
        {
            Player p = null;
            if(sender instanceof Player) p = (Player) sender;
            if(!Utils.HasPermission(p, "irc.manage"))
            {
                p.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }
            if(args.length == 0)
            {
                sender.sendMessage("Use /ircbot help for a list of commands.");
                return true;
            }
            
            
            if(args[0].equalsIgnoreCase("help"))
            {
                sender.sendMessage("Irc bot management commands:");
                sender.sendMessage("/ircbot clients: lists all currently operational clients");
                sender.sendMessage("/ircbot newclient <name>:  opens a new connection");
                sender.sendMessage("/ircbot setclient <client name>:  selects a certain client");
                sender.sendMessage("/ircbot connect [server] [port] [nick]: connects the currently selected client");
                sender.sendMessage("/ircbot join <channel>:  joins the client to a channel");
                sender.sendMessage("/ircbot part <channel>: parts the client from a channel");
                sender.sendMessage("/ircbot addactive <channel>: adds an \"active channel\" to the IRC client");
                sender.sendMessage("/ircbot quote <raw message>: sends a raw, unprocessed message to the server");
                sender.sendMessage("/ircbot delete <client name>: deletes an irc client");
                sender.sendMessage("/ircbot connect can be done with no arguments if the defaultclient in irc.properties is selected.");
                return true;
            }
            if(args[0].equalsIgnoreCase("addactive"))
            {
                if(Parent.Manager.getCurrentClient() == null) { sender.sendMessage(ChatColor.RED + "No client is currently selected."); return true;}
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot addactive <channel name>"); return true; }
                Parent.Manager.getCurrentClient().getMcEcho_ActiveChannels().add(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Active channel added.");
                return true;
            }
            if(args[0].equalsIgnoreCase("newclient"))
            {
                if(args.length < 2) return false;                
                Parent.Manager.getAllClients().add(new IrcClient(Parent, args[1]));
                sender.sendMessage(ChatColor.GREEN + "Client created.");
                return true;
            }
            if(args[0].equalsIgnoreCase("clients"))
            {
                sender.sendMessage(ChatColor.GREEN + "List of clients:");
                if(Parent.Manager.getCurrentClient() == null) sender.sendMessage(ChatColor.YELLOW + "(No client is currently selected)");
                for(IrcClient c : Parent.Manager.getAllClients())
                {
                    if((Parent.Manager.getCurrentClient() != null) && Parent.Manager.getCurrentClient().equals(c)) sender.sendMessage(ChatColor.GOLD + "Current client: " + ChatColor.YELLOW + c.getName());
                    else sender.sendMessage(ChatColor.YELLOW + c.getName());
                }
                
                return true;
            }
            if(args[0].equalsIgnoreCase("connect"))
            {
                if(Parent.Manager.getCurrentClient() == null) { sender.sendMessage(ChatColor.RED + "No client is currently selected."); return true;}
                if(args.length < 4 && args.length > 1){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot connect <server> <port> <nick>"); return true; }
                try                   
                {
                    IrcClient current = Parent.Manager.getCurrentClient();
                    if(args.length >= 4)
                    {
                        current.Connect(args[1], Integer.parseInt(args[2]));
                        current.Login(args[3], "ApnIrc", "Aprilon IRC Bukkit Plugin");
                        current.Listen();
                        sender.sendMessage(ChatColor.GREEN + "Connecting...");
                        return true;
                    }
                    if(args.length == 1)
                    {
                        if(current.Connect() == false) { sender.sendMessage(ChatColor.RED + "Server and/or port were unspecified in irc.properties, please use /ircbot connect <server> <port> <nick>."); return true; }
                        if(current.Login() == false) { sender.sendMessage(ChatColor.RED + "Nick, ident, or realname were unspecified in irc.properties, please use /ircbot connect <server> <port> <nick>."); return true; }
                        current.Listen();
                        sender.sendMessage(ChatColor.GREEN + "Connecting...");
                    }
                }
                catch(NumberFormatException e){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot connect <server> <port>"); return true; }
                catch(Exception e){ sender.sendMessage(ChatColor.RED + e.getMessage()); return true; }
            }
            if(args[0].equalsIgnoreCase("setclient"))
            {
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage: /ircbot setclient <client name>"); return true; }
                
                IrcClient client = Parent.Manager.getClient(args[1]);
                if(client == null){ sender.sendMessage(ChatColor.RED + "No client by that name exists!"); return true; }
                
                Parent.Manager.setCurrentClient(client);
                sender.sendMessage(ChatColor.GREEN + "Current client set.");
                
                return true;
            }
            if(args[0].equalsIgnoreCase("quote"))
            {
                if(Parent.Manager.getCurrentClient() == null) { sender.sendMessage(ChatColor.RED + "No client is currently selected."); return true;}
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot quote <quote>"); return true; }
            
                String quote = "";

                for(int i = 1; i <= (args.length - 1); i++) //start at 1 to avoid "quote"
                {
                    if(i != 0) quote += " ";
                    quote += args[i];
                }

                try
                {
                    Parent.Manager.getCurrentClient().getWriter().write(quote + "\r\n");
                    Parent.Manager.getCurrentClient().getWriter().flush();
                }
                catch(Exception e)
                {
                    sender.sendMessage(ChatColor.RED + "Unable to execute command:");
                    sender.sendMessage(e.toString());
                }

                return true;
            }
            
            if(args[0].equalsIgnoreCase("join"))
            {
                if(Parent.Manager.getCurrentClient() == null) { sender.sendMessage(ChatColor.RED + "No client is currently selected."); return true;}
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot join <channel>"); return false; }
                try{ Parent.Manager.getCurrentClient().Join(args[1]); }
                catch(Exception e)
                {
                    sender.sendMessage(ChatColor.RED + "Unable to execute command:");
                    sender.sendMessage(e.toString());
                }
                
                return true;
            }
            
            if(args[0].equalsIgnoreCase("part"))
            {
                if(Parent.Manager.getCurrentClient() == null) { sender.sendMessage(ChatColor.RED + "No client is currently selected."); return true;}
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot part <channel>");  return true; }
                
                try{ Parent.Manager.getCurrentClient().Part(args[1]); }
                catch(Exception e)
                {
                    sender.sendMessage(ChatColor.RED + "Unable to execute command:");
                    sender.sendMessage(e.toString());
                }
                
                return true;
            }
            
            if(args[0].equalsIgnoreCase("delete"))
            {
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "Usage:  /ircbot delete <name>"); return true; }
                
                IrcClient client = Parent.Manager.getClient(args[1]);
                if(client == null){ sender.sendMessage(ChatColor.RED + "No client by that name exists!"); return true; }
                
                client.Stop();
                Parent.Manager.getAllClients().remove(client);
                sender.sendMessage("Client stopped and deleted.");
            }
        }
        
        sender.sendMessage("Use /ircbot help for a list of commands.");
        return true;
    }
}
