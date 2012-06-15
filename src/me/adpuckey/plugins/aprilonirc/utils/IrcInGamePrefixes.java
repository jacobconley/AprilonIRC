package me.adpuckey.plugins.aprilonirc.utils;

import me.adpuckey.plugins.aprilonirc.irc.IrcUser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;
import java.util.ArrayList;

public class IrcInGamePrefixes {
    private static File PrefixesFile = new File("plugins/AprilonIrc/prefixes.txt");
    private static File SuffixesFile = new File("plugins/AprilonIrc/suffixes.txt");
    private static List<String> Prefixes = new ArrayList<String>();
    private static List<String> Suffixes = new ArrayList<String>();
    
    public static String DefaultPrefix;
    public static String DefaultSuffix;
    
    public static boolean Read(String hostname)
    {
        try
        {
            BufferedReader prefixes = new BufferedReader(new FileReader(PrefixesFile));            
            String line;
            while((line = prefixes.readLine()) != null)
            {
                System.out.println("prefixes - readline - " + line);//debug
                if(!line.contains(" ")){ Utils.Warning("Invalid line in prefixes.txt!"); continue; }
                String[] split = line.split(" ");
                if(split.length < 2){ Utils.Warning("Invalid line in prefixes.txt!"); continue; }
                
                final String host_trim = hostname.trim();
                final String file_trim = split[0].trim();
                if(file_trim.equalsIgnoreCase(host_trim)) Prefixes.add(file_trim + " " + split[1]);
                else//debug
                {
                    System.out.println("prefixes - no match");
                    System.out.println(host_trim);
                    System.out.println(file_trim);
                }
            }
            prefixes.close();
            
            BufferedReader suffixes = new BufferedReader(new FileReader(SuffixesFile));
            while((line = suffixes.readLine()) != null)
            {
                System.out.println("suffixes - readline - " + line);//debug
                if(!line.contains(" ")){ Utils.Warning("Invalid line in suffixes.txt!"); continue; }
                String[] split = line.split(" ");
                if(split.length < 2){ Utils.Warning("Invalid line in suffixes.txt!"); continue; }
                
                final String host_trim = hostname.trim();
                final String file_trim = split[0].trim();
                if(file_trim.equalsIgnoreCase(host_trim)) Suffixes.add(file_trim + " " + split[1]);
                else//debug
                {
                    System.out.println("prefixes - no match");
                    System.out.println(file_trim);
                    System.out.println(host_trim);
                }
            }
            suffixes.close();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException(e, "reading prefixes/suffixes");
            return false;
        }               
    }
    public static void Unread(String hostmask)
    {
        for(String s : Prefixes){ if(s.split(" ")[0].equalsIgnoreCase(hostmask)) Prefixes.remove(s); }
        for(String s : Suffixes){ if(s.split(" ")[0].equalsIgnoreCase(hostmask)) Suffixes.remove(s); }
    }
                
    public static String getPrefix(IrcUser u)
    {
        System.out.println("getprefix");//debug
        for(String s : Prefixes)
        {
            System.out.println("prefixindb - " + s);//
            String[] split = s.split(" ");
            if(u.getSource().equalsIgnoreCase(split[0])) return split[1].replaceAll("&", "§");
            else{//debug
                System.out.println("getprefix - no match");
                System.out.println(s);
                System.out.println(u.getSource());
            }
        }
        return DefaultPrefix.replaceAll("&", "§");
    }
    public static String getSuffix(IrcUser u)
    {
        System.out.println("getsuffix");//debug
        for(String s : Suffixes)
        {
            System.out.println("suffixindb - " + s);//debug
            String[] split = s.split(" ");
            if(u.getSource().equalsIgnoreCase(split[0])) return split[1].replaceAll("&", "§");
            else{//debug
                System.out.println("getsuffix - no match");
                System.out.println(s);
                System.out.println(u.getSource());
            }
        }
        return DefaultSuffix.replaceAll("&", "§");
    }
    
    public static boolean WritePrefix(String hostname, String prefix)
    {
        Prefixes.add(hostname + " " + prefix);
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PrefixesFile, true));
            writer.write(hostname + " " + prefix + "\r\n");
            writer.close();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException(e, "writing prefix");
            return false;
        }
    }
    public static boolean WriteSuffix(String hostname, String suffix)
    {
        Suffixes.add(hostname + " " + suffix);
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SuffixesFile, true));
            writer.write(hostname + " " + suffix + "\r\n");
            writer.close();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException(e, "writing suffix");
            return false;
        }
    }
    private static boolean Delete(File f, String line)
    {
        try
        {
            List<String> lines = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line_file;
            while((line_file = reader.readLine()) != null){ lines.add(line_file); }
            reader.close();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            for(String s : lines) { if(!s.equalsIgnoreCase(line)) writer.write(s  + "\r\n"); }
            writer.close();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException(e, "deleting prefix/suffix");
            return false;
        }
    }
    public static boolean DeletePrefix(String hostname)
    {
        System.out.println("delete");//debug
        Read(hostname);
        List<String> todel = new ArrayList<String>();//list to delete
        for(String s : Prefixes)
        {
            String[] split = s.split(" ");
            if(split[0].equalsIgnoreCase(hostname)){
                System.out.println("delete - match " + s);//debug
                todel.add(s);
                if(Delete(PrefixesFile, s) == false) return false;
            }
            else{//debug
                System.out.println("delete - nomatch");
                System.out.println(hostname);
                System.out.println(s);
            }
        }
        for(String s : todel) Prefixes.remove(s);
        return true;
    }
    public static boolean DeleteSuffix(String hostname)
    {
        Read(hostname);
        List<String> todel = new ArrayList<String>();//list to delete
        for(String s : Suffixes)
        {
            String[] split = s.split(" ");
            if(split[0].equalsIgnoreCase(hostname)){
                todel.add(s);
                if(Delete(SuffixesFile, (s)) == false) return false;
            }
        }
        for(String s : todel) Prefixes.remove(s);
        return true;
    }    
}
