package com.scorchedcode.DiscordSync;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlStaticConfig;

import javax.security.auth.login.LoginException;
import java.awt.*;
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
    protected void onPluginStop() {
        if(Settings.SERVER_RESTART_MESSAGE != null && !Settings.SERVER_RESTART_MESSAGE.isEmpty()) {
            WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            WebhookEmbedBuilder web = new WebhookEmbedBuilder();
            web.setTitle(new WebhookEmbed.EmbedTitle(Settings.SERVER_RESTART_MESSAGE, null));
            web.setColor(Color.DARK_GRAY.getRGB());
            msg.setAvatarUrl("https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png");
            msg.setUsername(Settings.GLOBAL_MESSAGE_USERNAME);
            msg.addEmbeds(web.build());
            client.send(msg.build());
        }
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
        //((TextChannel)channel).getManager().setTopic("Minecraft server version " + Bukkit.getBukkitVersion().replaceAll("-.+", "") + " & ip: " + (Settings.SERVER_DOMAIN.isEmpty() ? Bukkit.getIp() : Settings.SERVER_DOMAIN) + (WolfplzzFixes.DYNMAP_LINK != null ? " , Dynmap: " + WolfplzzFixes.DYNMAP_LINK : "")).complete();


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
        String statusRev = Settings.CUSTOM_STATUS.replaceAll("\\{players}", String.valueOf(DiscordSync.getInstance().getServer().getOnlinePlayers().size()));
        Activity stat = (Settings.CUSTOM_STATUS.contains("{playing}")) ? Activity.playing(statusRev.replaceAll("\\{playing}", "")) :
                (Settings.CUSTOM_STATUS.contains("{listening}")) ? Activity.listening(statusRev.replaceAll("\\{listening}", "")) :
                Activity.watching(statusRev.replaceAll("\\{watching}", ""));
        api.getPresence().setPresence(OnlineStatus.ONLINE, stat);
    }

    @Override
    public List<Class<? extends YamlStaticConfig>> getSettings() {
        List<Class<? extends YamlStaticConfig>> settings = new ArrayList<>();
        settings.add(Settings.class);
        return settings;
    }

    public static class API {

        public static void broadcast(TextComponent... args) {
            ArrayList<String> texts = new ArrayList<>();
            for(TextComponent text : args)
                texts.add((text.getClickEvent() != null) ? DarkUtil.getURL(text.getClickEvent().getValue()) : text.getText());
            channel.sendMessage(ChatColor.stripColor(Common.joinToString(texts).replaceAll("[\\[\\],]", ""))).complete();
            for(Player p : getInstance().getServer().getOnlinePlayers())
                p.spigot().sendMessage(args);
        }
    }

}