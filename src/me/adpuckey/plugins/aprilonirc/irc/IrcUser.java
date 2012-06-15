package me.adpuckey.plugins.aprilonirc.irc;

import java.util.List;
import java.util.ArrayList;

public class IrcUser {
    private String Source;
    private String Ident;
    private String Nick;
    private String Hostname;
    private List<String> OpChannels = new ArrayList<String>();
    private List<String> VoiceChannels = new ArrayList<String>();
    public String getSource() { return Source; }
    public String getIdent() { return Ident; }
    public String getNick() { return Nick; }
    public String getHostname() { return Hostname; }
    public List<String> getOpChannels(){ return OpChannels; }
    public List<String> getVoiceChannels(){ return VoiceChannels; }
    public void setNick(String nick){ this.Nick = nick; }
    
    private boolean IsOnline = true;
    public boolean getOnline(){ return IsOnline; }
    
    public void AddOp(String channel)
    {
        OpChannels.add(channel);
    }
    public void RemoveOp(String channel)
    {
        boolean remove = OpChannels.remove(channel);
    }
    public void AddVoice(String channel)
    {
        VoiceChannels.add(channel);
    }
    public void RemoveVoice(String channel)
    {
        VoiceChannels.remove(channel);
    }
    
    public void RemoveChannel(String channel)
    {
        if(VoiceChannels.contains(channel)) VoiceChannels.remove(channel);
        if(OpChannels.contains(channel)) OpChannels.remove(channel);
    }
    
    public void ParseSource(String source)
    {
        this.Source = source.replaceFirst(":","");
        
        String[] source_nicksplit = Source.split("!");
        Nick = source_nicksplit[0];
        
        String[] source_hostnamesplit = source_nicksplit[1].split("@");
        Ident = source_hostnamesplit[0];
        Hostname = source_hostnamesplit[1];
    }
    public static IrcUser New_ParseSource(String source)
    {
        IrcUser user = new IrcUser();
        user.Source = source;
        
        String[] source_nicksplit = source.replaceFirst(":","").split("!");
        user.Nick = source_nicksplit[0];
        
        String[] source_hostnamesplit = source_nicksplit[1].split("@");
        user.Ident = source_hostnamesplit[0];
        user.Hostname = source_hostnamesplit[1];
        
        return user;
    }
}
