package me.adpuckey.plugins.aprilonirc.irc;

public class IrcMessage {
    private IrcClient Client;
    private String Channel;
    private IrcUser User;
    private MessageTypes Type;
    private String RawMessage;
    private String Message;
    public IrcClient getClient() { return Client; }
    public String getChannel(){ return Channel; }
    public IrcUser getUser(){ return User; }
    public MessageTypes getType() { return Type; }
    public String getRawMessage(){ return RawMessage; }
    public String getMessage() { return Message; }
    public void setChannel(String channel){ this.Channel = channel; }
    public void setUser(IrcUser user){ this.User = user; }
    public void setMessage(String message){ this.Message = message; }
    
    public IrcMessage(IrcClient client, MessageTypes type, String rawmessage)
    {
        this.Client = client;
        this.Type = type;
        this.RawMessage = rawmessage;
    }
}
