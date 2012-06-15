package me.adpuckey.plugins.aprilonirc;

import me.adpuckey.plugins.aprilonirc.utils.Utils;
import me.adpuckey.plugins.aprilonirc.utils.SettingsUtils;
import me.adpuckey.plugins.aprilonirc.utils.ClientManager;
import me.adpuckey.plugins.aprilonirc.command.*;
import me.adpuckey.plugins.aprilonirc.event.*;

import org.bukkit.plugin.java.JavaPlugin;

public class AprilonIrc extends JavaPlugin{
    
    public IrcMessageHandler MessageHandler = new IrcMessageHandler(this);
    public IrcCommandHandler CommandHandler = new IrcCommandHandler(this);
    public ClientManager Manager = new ClientManager(this);

    @Override
    public void onDisable() {
        Utils.Info("Version " + this.getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        Mc_IrcBotCommands irccommands = new Mc_IrcBotCommands(this);  
        Mc_PlayerCommands playercommands = new Mc_PlayerCommands(this);
        getCommand("ircbot").setExecutor(irccommands);
        getCommand("irc").setExecutor(playercommands);
        
        IrcPlayerListener listener = new IrcPlayerListener(this);
        
        SettingsUtils.ReadMessageTypes_Properties();
        
        if(SettingsUtils.ReadIrc_Properties() == false)
        {
            Utils.Warning("Some required values in irc.properties were missing.");
        }        
        else if(SettingsUtils.getIfDefaultClient())
        {
            Utils.Info("Initializing default client...");
            SettingsUtils.InitializeDefaultClient(this);
            try
            {
                Utils.Info("Connecting to server...");
                if(SettingsUtils.getAutoConnect() == true)
                {
                    Manager.getCurrentClient().Connect();
                    Manager.getCurrentClient().Login();
                    Manager.getCurrentClient().Listen();
                }
            }
            catch(Exception e)
            {
                Utils.LogException(e, "connecting default client");
            }
        }
        
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
}
