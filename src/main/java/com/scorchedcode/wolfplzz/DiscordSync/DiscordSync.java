package com.scorchedcode.wolfplzz.DiscordSync;

import com.scorchedcode.wolfplzz.Fixes.WolfplzzFixes;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlStaticConfig;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class DiscordSync extends SimplePlugin {
    private static DiscordSync init;
    static String inviteURL = "";
    static Webhook hook;
    static MessageChannel channel;
    static MessageChannel helpOpChannel;
    private JDA api;

    public static DiscordSync getInstance() {
        return init;
    }

    @Override
    protected void onPluginStart() {

    }

    @Override
    protected void onReloadablesStart() {
        init = this;
        if((Settings.TOKEN == null) || (Settings.TOKEN.equals(""))) {
            getLogger().info("No bot token string found, disabling.");
            setEnabled(false);
            return;
        }
        initDiscordBot();
        List<TextChannel> channels = api.getTextChannelsByName(Settings.MC_ROOM_NAME, true);
        if(channels.size() == 1)
            channel = channels.get(0);
        else {
            getLogger().info("No channel with given name found, disabling.");
            setEnabled(false);
            return;
        }
        if(!Settings.HELPOP_ROOM.isEmpty())
            helpOpChannel = api.getTextChannelsByName(Settings.HELPOP_ROOM, true).get(0);
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
        ((TextChannel)channel).getManager().setTopic("Minecraft server version " + MinecraftVersion.getServerVersion().replaceAll("-.+", "") + " & ip: " + Bukkit.getIp() + " , Dynmap: ").complete();


    }

    private void initDiscordBot() {
        try {
            api = new JDABuilder(AccountType.BOT).setToken(Settings.TOKEN).build().awaitReady();
        } catch (LoginException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        api.addEventListener(new DiscordListener());
        setStatus();
    }

    public void setStatus() {
        String maintRev = Settings.MAINTENANCE_STATUS.replaceAll("\\{players}", String.valueOf(DiscordSync.getInstance().getServer().getOnlinePlayers().size()));
        String statusRev = Settings.CUSTOM_STATUS.replaceAll("\\{players}", String.valueOf(DiscordSync.getInstance().getServer().getOnlinePlayers().size()));
        Activity stat = (Settings.CUSTOM_STATUS.contains("{playing}")) ? Activity.playing(statusRev.replaceAll("\\{playing}", "")) :
                (Settings.CUSTOM_STATUS.contains("{listening}")) ? Activity.listening(statusRev.replaceAll("\\{listening}", "")) :
                Activity.watching(statusRev.replaceAll("\\{watching}", ""));
        Activity maint = (Settings.MAINTENANCE_STATUS.contains("{playing}")) ? Activity.playing(maintRev.replaceAll("\\{playing}", "")) :
                (Settings.MAINTENANCE_STATUS.contains("{listening}")) ? Activity.listening(maintRev.replaceAll("\\{listening}", "")) :
                Activity.watching(maintRev.replaceAll("\\{watching}", ""));
        api.getPresence().setPresence((WolfplzzFixes.MAINTENANCE_MODE) ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.ONLINE,
                (WolfplzzFixes.MAINTENANCE_MODE) ? maint : stat);
    }

    @Override
    public List<Class<? extends YamlStaticConfig>> getSettings() {
        List<Class<? extends YamlStaticConfig>> settings = new ArrayList<>();
        settings.add(Settings.class);
        return settings;
    }

}