package me.adpuckey.plugins.aprilonirc.utils;

import me.adpuckey.plugins.aprilonirc.AprilonIrc;
import me.adpuckey.plugins.aprilonirc.irc.IrcClient;
import java.util.List;
import java.util.ArrayList;

public class ClientManager {
    private AprilonIrc Parent;
    
    public ClientManager(AprilonIrc plugin){ Parent = plugin; }
    
    private List<IrcClient> AllClients = new ArrayList();
    private IrcClient CurrentClient = null;
    public List<IrcClient> getAllClients(){ return AllClients; }
    public IrcClient getCurrentClient(){ return CurrentClient; }
    public void setCurrentClient(IrcClient client){ CurrentClient = client; }
    public IrcClient getClient(String name){ for(IrcClient c : AllClients){ if(c.getName().equals(name)) return c; } return null; }
}
