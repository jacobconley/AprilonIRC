package me.adpuckey.plugins.aprilonirc.irc;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.utils.Utils;

public class IrcClient {    
    private AprilonIrc Parent;
    public AprilonIrc getParent(){ return Parent; }
    
    private String Name;    
    private String Server = null;
    private int Port = 6667;
    private boolean HasConnected = false;
    private List<String> Channels = new ArrayList();
    private List<IrcUser> Users = new ArrayList();
    private String Nick = null;
    private String Ident = null;
    private String RealName = null;    
    public boolean hasConnected(){ return HasConnected; }
    public String getName(){ return Name; }
    public String getNick(){ return Nick; }
    public List<String> getChannels(){ return Channels; }
    public List<IrcUser> getUsers(){ return Users; }
    public void setServer(String server){ this.Server = server; }
    public void setPort(int port){ this.Port = port; }
    public void setHasConnected(boolean hasconnected){ this.HasConnected = hasconnected; }
    public void setNick(String nick){ this.Nick = nick; }
    public void setIdent(String ident){ this.Ident = ident; }
    public void setRealName(String realname){ this.RealName = realname; }
    
    private int ReconnectAttempts = 3;
    public int getAttemptsLeft(){ return ReconnectAttempts; }
    public void decrementAttempts(){ ReconnectAttempts--; }
    public void restoreAttempts(){ ReconnectAttempts = 3; }
    
    public IrcUser getUser(String nick)
    {
        for(IrcUser u: Users)
        {
            if(u == null) continue;
            if(u.getNick() == null) continue;
            if(u.getNick().equals(nick)) return u;
        }
        return null;
    }
    public boolean AddUser(IrcUser user){ return Users.add(user);}
    
    private Socket Socket;
    private BufferedReader Reader;
    private BufferedWriter Writer;    
    public BufferedReader getReader(){ return Reader; }
    public BufferedWriter getWriter(){ return Writer; }
    
    private IrcListenerThread Listener;
    
    private String[] DefaultChannels;
    private List<String> McEcho_ActiveChannels = new ArrayList<String>();
    public List<String> getMcEcho_ActiveChannels(){ return McEcho_ActiveChannels; }
    public void setMcEcho_ActiveChannels(String[] set){ McEcho_ActiveChannels = Arrays.asList(set); }
    public void setDefaultChannels(String[] set){ DefaultChannels = set; }
    
    public IrcClient(AprilonIrc plugin, String name) { this.Parent = plugin; this.Name = name; }
    
    public boolean Connect(String server, int port) throws Exception
    {
        this.Server = server;
        this.Port = port;

        this.Socket = new Socket(server, port);

        Reader = new BufferedReader(new InputStreamReader(this.Socket.getInputStream()));
        Writer = new BufferedWriter(new OutputStreamWriter(this.Socket.getOutputStream()));

        Listener = new IrcListenerThread(Parent, this);

        return true;
    }
    public boolean Connect() throws Exception
    {
        if(Server == null) return false;
        
        this.Socket = new Socket(Server, Port);

        Reader = new BufferedReader(new InputStreamReader(this.Socket.getInputStream()));
        Writer = new BufferedWriter(new OutputStreamWriter(this.Socket.getOutputStream()));

        Listener = new IrcListenerThread(Parent, this);

        return true;
    }
        
    public boolean Login(String nick, String ident, String realname) throws Exception
    {
        this.Nick = nick;
        this.Ident = ident;
        this.RealName = realname;
        
        Writer.write("NICK " + nick + "\r\n");
        Writer.write("USER " + ident + " 8 * : " + realname + "\r\n");
        Writer.flush();
        
        return true;
    }
    public boolean Login() throws Exception
    {
        if((Nick == null) || (Ident == null) || (RealName == null)) return false;
        
        Writer.write("NICK " + Nick + "\r\n");
        Writer.write("USER " + Ident + " 8 * : " + RealName + "\r\n");
        Writer.flush();
        
        return true;
    }
        
    public void Listen(){ Listener.start(); }
    
    public void JoinDefault()
    {
        if(DefaultChannels == null || DefaultChannels.length == 0) return;
        
        try
        {
            for(String channel : DefaultChannels){ Join(channel); }
        }
        catch(Exception e)
        {
            Utils.LogException(e, "joining default channels");
        }
    }
    
    public void Join(String channel) throws Exception
    {
        Writer.write("JOIN " + channel + "\r\n");
        Writer.append("WHO " + channel);
        Writer.flush();
    }
    public void Part(String channel) throws Exception
    {        
        Writer.write("PART " + channel + "\r\n");
        Writer.flush();
    }
    
    public void PrivMsg(String destination, String message, boolean autothrow) throws Exception
    {
        try
        {
            Writer.write("PRIVMSG " + destination + " :" + message + "\r\n");
            Writer.flush();
        }
        catch(Exception e)
        {
            if(autothrow) throw e;
            Utils.LogException(e, "sending IRC message");
        }
    }
    public void Action(String destination, String message, boolean autothrow) throws Exception
    {
        final char actionchar = 0x01;
        try
        {
            Writer.write("PRIVMSG " + destination + " :ACTION" + actionchar + " " + message + " " + actionchar + "\r\n");
            Writer.flush();
        }
        catch(Exception e)
        {
            if(autothrow) throw e;
            Utils.LogException(e, "Sending IRC message");
        }
    }
    
    public void Stop()
    {
        try
        {
            Reader.close();
            Writer.close();
            Socket.close();
            Listener.stop();
        }
        catch(Exception e)
        {
            Utils.LogException(e, "Stopping client");
        }
    }
}
