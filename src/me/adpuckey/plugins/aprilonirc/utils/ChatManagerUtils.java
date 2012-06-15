package me.adpuckey.plugins.aprilonirc.utils;

import me.adpuckey.plugins.aprilonirc.irc.IrcColor;
import java.util.logging.Logger;

import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatManagerUtils {
    private static final Logger log = Logger.getLogger("");
    
    public static String ParseDisplayName(Player p)
    {
        PermissionManager manager = null;
        String displayNameFormat = "%prefix%player%suffix";
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) manager = PermissionsEx.getPermissionManager();
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("ChatManager")) displayNameFormat = Bukkit.getServer().getPluginManager().getPlugin("ChatManager").getConfig().getString("display-name-format");
        
        String displayname = p.getDisplayName();
        
        if(manager != null)
        {
            PermissionUser user = manager.getUser(p);
            String prefix = user.getPrefix().replaceAll("&", "§");
            String suffix = user.getSuffix().replaceAll("&", "§");
            displayname = displayNameFormat;
            displayname = displayname.replace("%prefix", prefix);
            displayname = displayname.replace("%player", p.getDisplayName());
            displayname = displayname.replace("%suffix", suffix);
        }
        
        return displayname;
    }
    
    public static String ParseMessage(Player p, String s)
    {
        PermissionManager manager = null;
        String messageFormat = "<%prefix%player%suffix> %message";
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) manager = PermissionsEx.getPermissionManager();
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("ChatManager")) messageFormat = Bukkit.getServer().getPluginManager().getPlugin("ChatManager").getConfig().getString("message-format");
        
        String message = "<" + p.getName() + "> " + s;
        
        if(manager != null)
        {
            PermissionUser user = manager.getUser(p);
            String prefix = user.getPrefix().replaceAll("&", "§");
            String suffix = user.getSuffix().replaceAll("&", "§");
            message = messageFormat;
            message = message.replace("%prefix", prefix);
            message = message.replace("%player", p.getDisplayName());
            message = message.replace("%suffix", suffix + IrcColor.NORMAL.getIRCColor());
            message = message.replace("%message", s);
        }
        
        return message;
    }
}
