package me.adpuckey.plugins.aprilonirc.irc;

import me.adpuckey.plugins.aprilonirc.*;
import me.adpuckey.plugins.aprilonirc.utils.Utils;
import java.io.BufferedReader;

public class IrcListenerThread implements Runnable{    
    private AprilonIrc Parent;  
    private IrcClient HostClient;
    private BufferedReader HostReader;
    
    private Thread thread;
    
    public IrcListenerThread(AprilonIrc plugin, IrcClient hostclient)
    {
        this.Parent = plugin;
        this.HostClient = hostclient;
        this.HostReader = hostclient.getReader();
        
        thread = new Thread(this);
    }    
    public void start(){ thread.start(); }
    private boolean stopping = false;
    public void stop(){ stopping = true; }
    
    public static String ParseNick(String source) { String[] nicksplit = source.replaceFirst(":","").split("!"); return nicksplit[0]; }

    @Override
    public void run() {
        try
        {
            String rawmessage = null;
            while((rawmessage = HostReader.readLine()) != null)
            {
                if(stopping) break;
                if(rawmessage.startsWith("PING"))
                {
                    HostClient.getWriter().write("PONG " + rawmessage.substring(5) + "\r\n");
                    HostClient.getWriter().flush();
                    Parent.MessageHandler.onIrcMessage(new IrcMessage(HostClient, MessageTypes.PING, rawmessage));
                    continue;
                }
                
                final String[] split = rawmessage.split(" ");
                
                if(split[1].equals("AUTH"))
                {
                    String message = Utils.ArrayToString(split, 2, 0);
                    IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.AUTH, rawmessage);
                    message_data.setMessage(message);
                    Parent.MessageHandler.onIrcMessage(message_data);
                }
                if(split[1].equals("NOTICE"))
                {
                    String message = Utils.ArrayToString(split, 4, 0);
                    IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.NOTICE, rawmessage);
                    message_data.setMessage(message);
                    Parent.MessageHandler.onIrcMessage(message_data);
                    continue;
                }
                
                if(split[1].equals("PRIVMSG"))
                {
                    final char actionchar = 0x01;
                    final String action_open = ":" + actionchar + "ACTION";
                    
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    else user.ParseSource(split[0]);
                    
                    if(split[2].startsWith("#"))
                    {
                        String channelname = split[2];
                        if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                        
                        if(split[3].equals(action_open))
                        {
                            String message = Utils.ArrayToString(split, 4, 0).replace(actionchar, ' ');
                            //Create a string that starts at position 4, as indicated by "split,4,0".
                            //Disclude these parts of the array because they desginate that the message is an action
                            IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.CHAN_ACTION, rawmessage);
                            message_data.setChannel(channelname);
                            message_data.setUser(user);
                            message_data.setMessage(message);
                            Parent.MessageHandler.onIrcMessage(message_data);
                            continue;
                        }
                        else
                        {
                            String message = Utils.ArrayToString(split, 3, 0).replaceFirst(":","");
                            IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.CHANMSG, rawmessage);
                            message_data.setChannel(channelname);
                            message_data.setUser(user);
                            message_data.setMessage(message);
                            Parent.MessageHandler.onIrcMessage(message_data);
                            continue;
                        }
                    }
                    else
                    {
                        if(split[3].equals(action_open))
                        {
                            String message = Utils.ArrayToString(split, 4, 0).replace(actionchar, ' ');
                            //Create a string that starts at position 4, as indicated by split,4,0.
                            //Disclude these parts of the array because they desginate that the message is an action
                            IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.QUERY_ACTION, rawmessage);
                            message_data.setUser(user);
                            message_data.setMessage(message);
                            Parent.MessageHandler.onIrcMessage(message_data);
                            continue;
                        }
                        else
                        {
                            String message = Utils.ArrayToString(split, 3, 0).replaceFirst(":","");
                            IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.QUERYMSG, rawmessage);
                            message_data.setUser(user);
                            message_data.setMessage(message);
                            Parent.MessageHandler.onIrcMessage(message_data);
                            continue;
                        }
                    }                    
                }
                
                                
                if(split[1].equals("JOIN"))
                {
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    else user.ParseSource(split[0]);
                    
                    String channelname = split[2];
                    if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.JOIN, rawmessage);
                    message.setChannel(channelname);
                    message.setUser(user);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;                    
                }
                if(split[1].equals("NICK"))
                {
                    final String nick = ParseNick(split[0]);
                    final String newnick = split[2].replaceFirst(":", "");
                    
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    else user.ParseSource(split[0]);
                    user.setNick(newnick);
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.NICK, rawmessage);
                    message.setMessage(nick);//set messsage to old nick for display
                    message.setUser(user);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }
                if(split[1].equals("PART"))
                {
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    else user.ParseSource(split[0]);
                    
                    final String channelname = split[2];
                    if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                    
                    user.RemoveChannel(channelname);
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.PART, rawmessage);
                    message.setChannel(channelname);
                    message.setUser(user);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }                                
                if(split[1].equals("KICK"))
                {
                    final String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    final String target_nick = split[3];
                    IrcUser target = HostClient.getUser(target_nick);
                                        
                    String channelname = split[2];
                    if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                    
                    if(target != null) target.RemoveChannel(channelname);
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.KICK, rawmessage);
                    message.setChannel(channelname);
                    message.setUser(user);
                    message.setMessage(target_nick);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }
                if(split[1].equals("TOPIC"))
                {
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    
                    String channelname = split[2];
                    if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.TOPIC_CHANGE, rawmessage);
                    message.setChannel(channelname);
                    message.setUser(user);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }
                
                if(split[1].equals("QUIT"))
                {
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.QUIT, rawmessage);
                    message.setUser(user);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }
                
                if(split[1].equals("MODE"))
                {
                    if(split[0].equals(":" + HostClient.getNick()))
                    {
                        IrcMessage message = new IrcMessage(HostClient, MessageTypes.SELFMODE, rawmessage);
                        message.setMessage(split[3].replaceFirst(":", ""));
                        continue;
                    }
                    String nick = ParseNick(split[0]);
                    IrcUser user = HostClient.getUser(nick);
                    if(user == null){ user = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(user); }
                    
                    String channelname = split[2];
                    if(!HostClient.getChannels().contains(channelname)) HostClient.getChannels().add(channelname);
                    
                    String mode = split[3].replaceFirst(":", "");
                    String objectuser_nick= null;
                    if(split.length >= 5) objectuser_nick = split[4];
                    if(mode.equals("+v"))
                    {                        
                        IrcUser objectuser  = HostClient.getUser(objectuser_nick);
                        if(objectuser == null){ objectuser = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(objectuser); }
                        objectuser.AddVoice(channelname);
                    }
                    if(mode.equals("-v"))
                    {
                        IrcUser objectuser  = HostClient.getUser(objectuser_nick);
                        if(objectuser == null){ objectuser = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(objectuser); }
                        objectuser.RemoveVoice(channelname);
                    }
                    if(mode.equals("+o"))
                    {
                        IrcUser objectuser  = HostClient.getUser(objectuser_nick);
                        if(objectuser == null){ objectuser = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(objectuser); }
                        objectuser.AddOp(channelname);
                    }
                    if(mode.equals("-o"))
                    {
                        IrcUser objectuser  = HostClient.getUser(objectuser_nick);
                        if(objectuser == null){ objectuser = IrcUser.New_ParseSource(split[0]); HostClient.getUsers().add(objectuser); }
                        objectuser.RemoveOp(channelname);
                    }
                    
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.MODE, rawmessage);
                    message.setMessage(Utils.ArrayToString(split, 3, 0));
                    message.setUser(user);
                    message.setChannel(channelname);
                    Parent.MessageHandler.onIrcMessage(message);
                    continue;
                }
                                
                //Numeric IRC code messages beyond this.
                int nc = 0;
                try { nc = Integer.parseInt(split[1]); }
                catch(NumberFormatException e){ Parent.MessageHandler.onIrcMessage(new IrcMessage(HostClient, MessageTypes.UNKNOWN, rawmessage)); continue; }
                
                if(nc==301 || nc==310 || nc==311 || nc==312 || nc==313 || nc==317 || nc==319 || nc==431)
                {
                    IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.WHOIS, rawmessage);
                    String message = Utils.ArrayToString(split, 1, 0);
                    message_data.setMessage(message);
                    
                    Parent.MessageHandler.onIrcMessage(message_data);
                    continue;
                }
                if(nc == 318)
                {
                    IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.WHOIS_END, rawmessage);
                    String message = Utils.ArrayToString(split, 2, 0);
                    message_data.setMessage(message);
                    
                    Parent.MessageHandler.onIrcMessage(message_data);
                    continue;
                }
                if(nc == 333)
                {
                    IrcMessage message = new IrcMessage(HostClient, MessageTypes.TOPIC_SET_BY, rawmessage);
                    message.setChannel(split[3]);
                    IrcUser user = HostClient.getUser(split[4]);
                    if(user == null) message.setUser(user);
                    else
                    {
                        user = new IrcUser();
                        user.setNick(split[4]);
                        message.setUser(user);
                    }                    
                    message.setMessage(split[5]);
                    
                    Parent.MessageHandler.onIrcMessage(message);
                }
                if(nc == 353)
                {
                    //NAMES list
                    for(int i = 6; i <= (split.length - 1); i++)
                    {
                        String nick = split[i];
                        boolean op = false;
                        boolean voice = false;
                        boolean isnew = false;
                        if(nick.startsWith("@")){ op = true; nick = nick.replaceFirst("@", ""); }
                        if(nick.startsWith("+")){ voice = true; nick = nick.replaceFirst("\\+", ""); }
                        
                        IrcUser user = HostClient.getUser(nick);
                        if(user == null)
                        {
                            user = new IrcUser();
                            user.setNick(nick);
                            isnew = true;
                        }
                        
                        if(op) user.AddOp(split[4]);
                        if(voice) user.AddVoice(split[4]);
                        if(isnew) HostClient.AddUser(user);
                    }
                    
                    continue;
                }
                if(nc == 433)
                {
                    HostClient.getWriter().write("NICK " + HostClient.getNick() + "_\r\n");
                    HostClient.getWriter().flush();
                }
                if(nc == 372)
                {
                    //MOTD
                    IrcMessage message_data = new IrcMessage(HostClient, MessageTypes.MOTD, rawmessage);
                    String message = Utils.ArrayToString(split, 2, 0).replaceFirst(":","");
                    message_data.setMessage(message);
                    Parent.MessageHandler.onIrcMessage(message_data);
                    continue;
                }
                if(nc == 376)
                {
                    //END MOTD
                    if(!HostClient.hasConnected()) HostClient.JoinDefault();
                    HostClient.setHasConnected(true);
                    HostClient.restoreAttempts();
                    Parent.MessageHandler.onIrcMessage(new IrcMessage(HostClient, MessageTypes.END_MOTD, rawmessage));
                    continue;
                }
                
                Parent.MessageHandler.onIrcMessage(new IrcMessage(HostClient, MessageTypes.UNKNOWN, rawmessage)); //Undetected message type
            }
        }
        catch(Exception e)
        {
            Utils.LogException(e, "reading from IRC socket");
            HostClient.setHasConnected(false);
            while(HostClient.getAttemptsLeft() != 0)
            {
                Utils.Info("Attempting to reconnect...");
                try
                {
                    if(HostClient.Connect() == false){ Utils.Severe("Error: Address null while attempting reconnect."); return; }
                    if(HostClient.Login() == false){ Utils.Severe("Error: Nick, ident, or realname were null while attempting reconnect."); return; }
                    HostClient.Listen();
                    break;
                    
                }
                catch(Exception ex)
                {
                    Utils.LogException(ex, "attempting to reconnect.");                    
                }
                
                HostClient.decrementAttempts();
            }
            return;
        }
    }
}
