package me.adpuckey.plugins.aprilonirc;

import me.adpuckey.plugins.aprilonirc.irc.IrcMessage;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import me.adpuckey.plugins.aprilonirc.irc.IrcUser;
import me.adpuckey.plugins.aprilonirc.utils.SettingsUtils;
import me.adpuckey.plugins.aprilonirc.utils.IrcInGamePrefixes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class IrcMessageHandler {
    private static final Logger log = Logger.getLogger("");
    
    private AprilonIrc Parent;
    public IrcMessageHandler(AprilonIrc plugin){ Parent = plugin; }
    
    public void BroadcastInGame(String message)
    { for(Player p : Parent.getServer().getOnlinePlayers()){ p.sendMessage("[" + ChatColor.DARK_GREEN + "IRC" + ChatColor.WHITE + "] " + message); } }
    public void BroadcastInGame(String message, boolean space)//broadcast without a space after [IRC]
    { for(Player p : Parent.getServer().getOnlinePlayers()){ p.sendMessage("[" + ChatColor.DARK_GREEN + "IRC" + ChatColor.WHITE + "]" + message); } }
    
    static List<String> CheckedHostmasks = new ArrayList<String>();
    public static void ClearCheckedHostmasks(){ CheckedHostmasks.clear(); }
    
    public void onIrcMessage(IrcMessage e)
    {
        switch(e.getType())
        {
            case JOIN:
                IrcInGamePrefixes.Read(e.getUser().getSource());
                CheckedHostmasks.add(e.getUser().getSource());
                log.info(e.getUser().getSource() + " has joined " + e.getChannel());
                BroadcastInGame(e.getUser().getNick() + " has joined " + e.getChannel());
                return;
            case CHANMSG:
                final IrcUser user = e.getUser();                
                if(!CheckedHostmasks.contains(user.getSource()))
                {
                    IrcInGamePrefixes.Read(user.getSource());
                    CheckedHostmasks.add(user.getSource());
                }
                
                String prefix = IrcInGamePrefixes.getPrefix(user);
                String suffix = IrcInGamePrefixes.getSuffix(user);
                String nick_prefix =  "<%rank%prefix%nick%suffix> ";;//prefix has two different meanings here.  nick_prefix is the nickname displayed ingame. too lazy to rename.
                
                if(user.getOpChannels().contains(e.getChannel())) nick_prefix = nick_prefix.replace("%rank", "@");
                else if(user.getVoiceChannels().contains(e.getChannel())) nick_prefix = nick_prefix.replace("%rank", "@");
                else nick_prefix = nick_prefix.replace("%rank", "");
                nick_prefix = nick_prefix.replace("%prefix", prefix);
                nick_prefix = nick_prefix.replace("%suffix", suffix);
                nick_prefix = nick_prefix.replace("%nick", user.getNick());
                
                if(e.getMessage().startsWith("."))
                {
                    String[] split = e.getMessage().split(" ");
                    if(Parent.CommandHandler.onCommand(split, e) == false)
                    {
                        try { e.getClient().PrivMsg(e.getChannel(), "Unrecognized command. For a list of commands use .help", false); }
                        catch(Exception ex){}
                    }
                }
                else if(e.getClient().getMcEcho_ActiveChannels().contains(e.getChannel()))
                {
                    if(SettingsUtils.GetMessageTypes_Properties().getProperty("channel_message").equals("true")) log.info("[" + e.getChannel() + "]" + nick_prefix + e.getMessage());
                    BroadcastInGame(nick_prefix + e.getMessage(), false);
                }
                
                return;
            case CHAN_ACTION:
                log.info("* " + e.getUser().getNick() + " " + e.getMessage());
                for(Player p : Parent.getServer().getOnlinePlayers()){ p.sendMessage("[" + ChatColor.DARK_GREEN + "IRC" + ChatColor.WHITE + "] *" + e.getUser().getNick() + " " + e.getMessage()); }
                return;
            case QUERYMSG:
                log.info(">" + e.getUser().getNick() + "< " + e.getMessage());
                
                if(e.getMessage().startsWith("."))
                {
                    String[] split = e.getMessage().split(" ");
                    if(Parent.CommandHandler.onCommand(split, e) == false)
                    {
                        try { e.getClient().PrivMsg(e.getUser().getNick(), "Unrecognized command. For a list of commands use .help", false); }
                        catch(Exception ex){}
                    }
                }
                return;
            case QUERY_ACTION:
                log.info("QUERY: * " + e.getUser().getNick() + " " + e.getMessage());
                return;
            case PART:
                IrcInGamePrefixes.Unread(e.getUser().getSource());
                CheckedHostmasks.remove(e.getUser().getSource());
                log.info(e.getUser().getNick() + " has left " + e.getChannel());
                BroadcastInGame(e.getUser().getNick() + " has left " + e.getChannel());
                return;
            case KICK:
                IrcInGamePrefixes.Unread(e.getUser().getHostname());
                log.info(e.getUser().getNick() + " has kicked " + e.getMessage() + " from " + e.getChannel());
                BroadcastInGame(e.getUser().getNick() + " has kicked " + e.getMessage() + " from " + e.getChannel());
                return;                
            case QUIT:
                IrcInGamePrefixes.Unread(e.getUser().getHostname());
                log.info(e.getUser().getNick() + " has quit.");
                BroadcastInGame(e.getUser().getNick() + " has quit.");
                return;
            case NICK:
                log.info(e.getMessage() + " changed nick to " + e.getUser().getNick());
                BroadcastInGame(e.getMessage() + " changed nick to " + e.getUser().getNick());
                return;
            case MODE:
                log.info(e.getUser().getNick() + " sets mode " + e.getMessage() + " for channel " + e.getChannel());
                BroadcastInGame(e.getUser().getNick() + " sets mode " + e.getMessage() + " for channel " + e.getChannel());
                return;
            case SELFMODE:
                log.info("Mode " + e.getMessage() + " set.");
                return;
            case MOTD:
                log.info(e.getMessage());
                return;
            case END_MOTD:
                log.info("End of MOTD.");
                return;
            case AUTH:
                log.info(e.getMessage());
                return;
            case NOTICE:
                log.info(e.getMessage());
                return;
            case NUMERIC_CODE:
                log.info(e.getRawMessage());
                return;
            case UNKNOWN:
                log.info(e.getRawMessage());
                return;
        }
    }
}
