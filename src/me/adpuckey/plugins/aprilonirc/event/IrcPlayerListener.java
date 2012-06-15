package me.adpuckey.plugins.aprilonirc.event;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.IrcClient;
import me.adpuckey.plugins.aprilonirc.irc.IrcColor;
import me.adpuckey.plugins.aprilonirc.utils.ChatManagerUtils;

import me.adpuckey.plugins.aprilonirc.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class IrcPlayerListener implements Listener{
    private AprilonIrc Parent;
    
    public IrcPlayerListener(AprilonIrc plugin)
    {
        Parent = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        IrcClient client = Parent.Manager.getCurrentClient();
        for(String s : client.getMcEcho_ActiveChannels())
        {
            try{ client.PrivMsg(s, IrcColor.formatMCMessage(ChatManagerUtils.ParseDisplayName(e.getPlayer())) + IrcColor.NORMAL.getIRCColor() + " has joined.", false); }
            catch(NoClassDefFoundError ex){ ex.printStackTrace(); }
            catch(Exception ex){ }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(PlayerChatEvent e)
    {
        IrcClient client = Parent.Manager.getCurrentClient();
        for(String s : client.getMcEcho_ActiveChannels())
        {
            try{ client.PrivMsg(s, IrcColor.formatMCMessage(ChatManagerUtils.ParseMessage(e.getPlayer(), e.getMessage())), false); }
            catch(NoClassDefFoundError ex){ ex.printStackTrace(); }
            catch(Exception ex){ }
        }
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        String[] split = e.getMessage().split(" ");
        if(!split[0].equalsIgnoreCase("/me")) return;
        e.setCancelled(true);
        String message = Utils.ArrayToString(split, 1, 0);
        String message_labeled = "* " + e.getPlayer().getName() + " " + message;
        
        for(Player p : e.getPlayer().getServer().getOnlinePlayers()){ p.sendMessage(message_labeled); }
        
        IrcClient client = Parent.Manager.getCurrentClient();
        for(String s : client.getMcEcho_ActiveChannels())
        {
            try{ client.PrivMsg(s, IrcColor.formatMCMessage(message_labeled), false); }
            catch(NoClassDefFoundError ex){ ex.printStackTrace(); }
            catch(Exception ex){ }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        IrcClient client = Parent.Manager.getCurrentClient();
        for(String s : client.getMcEcho_ActiveChannels())
        {
            try{ client.PrivMsg(s, IrcColor.formatMCMessage(ChatManagerUtils.ParseDisplayName(e.getPlayer())) + IrcColor.NORMAL.getIRCColor() + " has left.", false); }
            catch(Exception ex){ }
        }
    }
}
