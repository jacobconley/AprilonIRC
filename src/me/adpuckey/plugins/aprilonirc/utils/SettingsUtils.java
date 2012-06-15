package me.adpuckey.plugins.aprilonirc.utils;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.IrcClient;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class SettingsUtils {
    
    private static File irc_properties_file = new File("plugins/AprilonIrc/irc.properties");
    private static Properties Irc_Properties = new Properties();
    public static Properties GetIrc_Properties(){ return Irc_Properties; }
    private static File messagetypes_properties_file = new File("plugins/AprilonIrc/messagetypes.properties");
    private static Properties messagetypes_properties = new Properties();
    public static Properties GetMessageTypes_Properties(){ return messagetypes_properties; }
    
    private static boolean DefaultClient = false;
    private static boolean AutoConnect = false;
    private static String Server = null;
    private static String Nick = null;
    private static String Ident = null;
    private static String RealName = null;
    private static int Port = 6667;
    private static String[] DefaultChannels;
    private static String[] McEcho_ActiveChannels;
    public static boolean getIfDefaultClient(){ return DefaultClient; }
    public static boolean getAutoConnect(){ return AutoConnect; }
    
    
    public static boolean ReadIrc_Properties()
    { //Returns false if there is a required property missing.        
        if(!irc_properties_file.exists())
        {
            Utils.Warning("Could not find plugins/AprilonIrc/irc.properties!");
            return true;
        }
        
        try
        {
            InputStream reader = new FileInputStream(irc_properties_file);
            Irc_Properties.load(reader);
        }
        catch(Exception e)
        {
            Utils.LogException(e, "reading irc.properties");
            return true;
        }
        
        if(!Irc_Properties.getProperty("defaultclient").equals("true"))
        {
            DefaultClient = false;
            AutoConnect = false;
            return true;
        }
        else DefaultClient = true;
        
        if(Irc_Properties.getProperty("autoconnect").equals("true")) AutoConnect = true;
        if(Irc_Properties.getProperty("server") != null) Server = Irc_Properties.getProperty("server");
        else return false;
        if(Irc_Properties.getProperty("nick") != null) Nick = Irc_Properties.getProperty("nick");
        else return false;
        if(Irc_Properties.getProperty("ident") != null) Ident = Irc_Properties.getProperty("ident");
        else return false;
        if(Irc_Properties.getProperty("realname") != null) RealName = Irc_Properties.getProperty("realname");
        else return false;
        
        String autojoin = Irc_Properties.getProperty("autojoin");
        if(autojoin != null)
        {
            if(autojoin.contains(",")) DefaultChannels = autojoin.split(",");
            else
            {
                DefaultChannels = new String[1];
                DefaultChannels[0] = autojoin;
            }
        }
        
        String active_channels = Irc_Properties.getProperty("active_channels");
        if(active_channels != null)
        {
            if(active_channels.contains(",")) McEcho_ActiveChannels = active_channels.split(",");
            
            {
                McEcho_ActiveChannels = new String[1];
                McEcho_ActiveChannels[0] = active_channels; 
            }
        }
        
        IrcInGamePrefixes.DefaultPrefix = Irc_Properties.getProperty("default_ingame_prefix");
        IrcInGamePrefixes.DefaultSuffix = Irc_Properties.getProperty("default_ingame_suffix");
        
        try
        {
            if(Irc_Properties.getProperty("port") != null) Port = Integer.parseInt(Irc_Properties.getProperty("port"));
            else return false;
        }
        catch(NumberFormatException e)
        {
            Utils.Severe("Port in irc.properties is not a number! Defaulting to 6667...");
            return true;
        }
        
        return true;
    }   
    public static void InitializeDefaultClient(AprilonIrc plugin)
    {
        if(DefaultClient == false) return;
        
        IrcClient defaultclient = new IrcClient(plugin, "defaultclient");
        defaultclient.setServer(Server);
        defaultclient.setPort(Port);
        defaultclient.setNick(Nick);
        defaultclient.setIdent(Ident);
        defaultclient.setRealName(RealName);
        defaultclient.setDefaultChannels(DefaultChannels);
        defaultclient.setMcEcho_ActiveChannels(McEcho_ActiveChannels);
        
        plugin.Manager.getAllClients().add(defaultclient);
        plugin.Manager.setCurrentClient(defaultclient);
    }
    
    public static void ReadMessageTypes_Properties()
    {
        if(!irc_properties_file.exists())
        {
            Utils.Warning("Could not find plugins/AprilonIrc/messagetypes.properties!");
            return;
        }
        
        try
        {
            InputStream reader = new FileInputStream(messagetypes_properties_file);
            messagetypes_properties.load(reader);
        }
        catch(Exception e)
        {
            Utils.LogException(e, "reading messagetypes.properties");
            return;
        }
    }
}
