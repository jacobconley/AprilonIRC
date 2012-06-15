package me.adpuckey.plugins.aprilonirc.command;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.*;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class IrcCommandSender implements CommandSender{
    private static final Logger log = Logger.getLogger("");
    private final AprilonIrc Plugin;
    private final IrcClient Client;
    private final IrcMessage Message;
    private final IrcUser User;
    
    public IrcCommandSender(AprilonIrc plugin, IrcClient client, IrcMessage message)
    {
        this.Plugin = plugin;
        this.Client = client;
        this.Message = message;
        this.User = message.getUser();
    }

    @Override
    public void sendMessage(String string) {
        try
        {
            if(Message.getType() == MessageTypes.QUERYMSG) Client.PrivMsg(User.getNick(), string, false);
            else Client.PrivMsg(Message.getChannel(), string, true);
        }
        catch(Exception e){ }
    }
    @Override
    public void sendMessage(String[] string)
    {
        for(String s : string){ sendMessage(s); }
    }

    @Override
    public Server getServer() { return Plugin.getServer(); }

    @Override
    public String getName() { return Message.getUser().getNick(); }

    @Override
    public boolean isPermissionSet(String string) { throw new UnsupportedOperationException("Not supported yet."); }
    
    @Override
    public boolean isPermissionSet(Permission prmsn) { throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public boolean hasPermission(String string) {
        if(User.getOpChannels().contains(Message.getChannel())) return true;
        else return false;
    }

    @Override
    public boolean hasPermission(Permission prmsn) {
        if(User.getOpChannels().contains(Message.getChannel())) return true;
        else return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public void removeAttachment(PermissionAttachment pa){ throw new UnsupportedOperationException("Not supported yet."); }
    
    @Override
    public void recalculatePermissions(){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions(){ throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public boolean isOp() {
        if(User.getOpChannels().contains(Message.getChannel())) return true;
        else return false;
    }

    @Override
    public void setOp(boolean bln){ throw new UnsupportedOperationException("Not supported yet."); }
}
