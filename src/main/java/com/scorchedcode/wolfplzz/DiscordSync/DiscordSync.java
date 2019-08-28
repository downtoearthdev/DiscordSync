package com.scorchedcode.wolfplzz.DiscordSync;

import com.scorchedcode.wolfplzz.Fixes.WolfplzzFixes;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.managers.WebhookManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.List;

public class DiscordSync extends JavaPlugin {
    static DiscordSync init;
    static String inviteURL = "";
    static Webhook hook;
    static String status;;
    static String maintenanceStatus;
    private static String TOKEN;
    static Guild GUILD;
    static MessageChannel channel;
    static MessageChannel helpOpChannel;
    private JDA api;


    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        init = this;
        saveDefaultConfig();
        TOKEN = getConfig().getString("token", null);
        if((TOKEN == null) || (TOKEN.equals(""))) {
            getLogger().info("No bot token string found, disabling.");
            setEnabled(false);
            return;
        }
        initDiscordBot();
        List<TextChannel> channels = api.getTextChannelsByName(getConfig().getString("mc-room-name", ""), true);
        if(channels.size() == 1)
            channel = channels.get(0);
        else {
            getLogger().info("No channel with given name found, disabling.");
            setEnabled(false);
            return;
        }
        if(!getConfig().getString("helpop-room").isEmpty())
            helpOpChannel = api.getTextChannelsByName(getConfig().getString("helpop-room"), true).get(0);
        getServer().getPluginManager().registerEvents(new MinecraftChatListener(), this);
        for(Webhook web : ((TextChannel) channel).retrieveWebhooks().complete()) {
            if(web.getName().equals("DiscordSync"))
                hook = web;
        }
        if(hook == null)
            hook = ((TextChannel) channel).createWebhook("DiscordSync").complete();
        for(Invite inv : ((TextChannel) channel).retrieveInvites().complete()) {
            if(inv.getInviter().getName().equals(api.getSelfUser().getName()))
                inviteURL = inv.getUrl();
        }
        if(inviteURL.isEmpty())
            inviteURL = ((TextChannel) channel).createInvite().setMaxAge(0).complete().getUrl();
        ((TextChannel)channel).getManager().setTopic("Minecraft server version " + Bukkit.getBukkitVersion().replaceAll("-.+", "") + " & ip: " + Bukkit.getIp() + " , Dynmap: ").complete();

    }

    private void initDiscordBot() {
        try {
            api = new JDABuilder(AccountType.BOT).setToken(TOKEN).build().awaitReady();
        } catch (LoginException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        api.addEventListener(new DiscordListener());
        //GUILD = api.getGuildById("287296012714770432")
        maintenanceStatus = getConfig().getString("maintenance-status", "Subscribe!");
        status = getConfig().getString("custom-status", "Subscribe!");
        setStatus();
    }

    public void setStatus() {
        String maintRev = maintenanceStatus.replaceAll("\\{players}", String.valueOf(DiscordSync.init.getServer().getOnlinePlayers().size()));
        String statusRev = status.replaceAll("\\{players}", String.valueOf(DiscordSync.init.getServer().getOnlinePlayers().size()));
        Activity stat = (status.contains("{playing}")) ? Activity.playing(statusRev.replaceAll("\\{playing}", "")) :
                (status.contains("{listening}")) ? Activity.listening(statusRev.replaceAll("\\{listening}", "")) :
                Activity.watching(statusRev.replaceAll("\\{watching}", ""));
        Activity maint = (maintenanceStatus.contains("{playing}")) ? Activity.playing(maintRev.replaceAll("\\{playing}", "")) :
                (maintenanceStatus.contains("{listening}")) ? Activity.listening(maintRev.replaceAll("\\{listening}", "")) :
                Activity.watching(maintRev.replaceAll("\\{watching}", ""));
        api.getPresence().setPresence((WolfplzzFixes.MAINTENANCE_MODE) ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.ONLINE,
                (WolfplzzFixes.MAINTENANCE_MODE) ? maint : stat);
    }

}