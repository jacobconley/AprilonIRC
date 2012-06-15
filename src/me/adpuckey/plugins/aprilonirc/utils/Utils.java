package me.adpuckey.plugins.aprilonirc.utils;

import java.util.logging.Logger;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.Server;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Utils {
    private static final Logger log = Logger.getLogger("");
    
    public static boolean wildCardMatch(String text, String pattern)
    {
        //from http://www.adarshr.com/papers/wildcard
        // Create the cards by splitting using a RegEx. If more speed 
        // is desired, a simpler character based splitting can be done.
        String [] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards)
        {
            int idx = text.indexOf(card);
            
            // Card not detected in the text.
            if(idx == -1)
            {
                return false;
            }
            
            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }
        
        return true;
    }
    
    public static void Info(String s){ log.info("[AprilonIRC] " + s);}
    public static void Warning(String s){ log.info("[AprilonIRC] " + s); }
    public static void Severe(String s){ log.info("[AprilonIRC] " + s); }
    public static void LogException(Exception e, String action)
    {
        Severe("Exception while " + action);
        Severe(e.toString());
        e.printStackTrace();
    }
    
    public static boolean HasPermission(Player p, String permission)
    {
        if(p == null) return true;
        if(p.hasPermission(permission)) return true;
        if(PermissionsEx.getPermissionManager().has(p, permission)) return true;
        
        return false;
    }
    
    public static String ArrayToString(String[] array, int bottomlimit, int toplimit)
    {
        String string = "";
        
        for(int i = bottomlimit; i <= (array.length - (1 + toplimit)); i++)
        {
            if (i != bottomlimit) string += " ";
            string += array[i];
        }
        
        return string;
    }
    
    public static Player NarrowMatchPlayer(Server server, String nick)
    {
        List<Player> list = server.matchPlayer(nick);
        if(list.size() > 1 || list.isEmpty()) return null;
        else return list.get(0);
    }
}
