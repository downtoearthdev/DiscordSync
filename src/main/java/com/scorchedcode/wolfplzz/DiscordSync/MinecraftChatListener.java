package com.scorchedcode.wolfplzz.DiscordSync;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.scorchedcode.wolfplzz.Fixes.WolfplzzFixes;
import com.scorchedcode.wolfplzz.Fixes.events.MaintenanceEvent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerLoadEvent;

import java.awt.*;
import java.util.List;

public class MinecraftChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaintenanceEvent(MaintenanceEvent e) {
        if(e.isMaintenanceEnabled()) {
            for(Message pins : DiscordSync.channel.retrievePinnedMessages().complete()) {
                if(pins.getContentRaw().contains("Server maintenance")) {
                    List<Message> msgs = DiscordSync.channel.getIterableHistory().complete();
                    msgs.get(msgs.indexOf(pins)-1).delete().complete();
                    pins.unpin().complete();
                    pins.delete().complete();
                }
            }
            DiscordSync.init.setStatus();
            Message msg = DiscordSync.channel.sendMessage("Server maintenance is occurring.").complete();
            msg.pin().complete();
        }
        else {
            for(Message pins : DiscordSync.channel.retrievePinnedMessages().complete()) {
                if(pins.getContentRaw().contains("Server maintenance")) {
                    List<Message> msgs = DiscordSync.channel.getIterableHistory().complete();
                    msgs.get(msgs.indexOf(pins)-1).delete().complete();
                    pins.unpin().complete();
                    pins.delete().complete();
                }
            }
            DiscordSync.init.setStatus();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerStart(ServerLoadEvent e) {
        if(DiscordSync.init.getConfig().getString("server-start-message", null) != null && !DiscordSync.init.getConfig().getString("server-start-message").isEmpty()) {
            List<Message> msgs = DiscordSync.channel.getIterableHistory().complete();
            if(msgs.get(0).getEmbeds().size() > 0) {
                if (msgs.get(0).getEmbeds().get(0).getTitle().contains(DiscordSync.init.getConfig().getString("server-start-message")))
                    msgs.get(0).delete().queue();
            }
            WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            WebhookEmbedBuilder web = new WebhookEmbedBuilder();
            web.setTitle(new WebhookEmbed.EmbedTitle(DiscordSync.init.getConfig().getString("server-start-message"), null));
            web.setColor(Color.DARK_GRAY.getRGB());
            msg.setAvatarUrl("https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png");
            msg.setUsername(DiscordSync.init.getConfig().getString("global_message_username", "Server"));
            msg.addEmbeds(web.build());
            client.send(msg.build());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHelpOpMessaage(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().contains("helpop ")) {
            if(DiscordSync.helpOpChannel != null && e.getMessage().split(" ").length > 1) {
                DiscordSync.helpOpChannel.sendMessage("[HelpOpRelay]<" + e.getPlayer().getName() + "> " + e.getMessage().replaceAll("/helpop", "")).queue();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("disable_mention_all", false) && (e.getMessage().contains("@here") || e.getMessage().contains("@everyone"))) {
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to mention all users.");
            e.setCancelled(true);
            return;
        }
        WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
        WebhookMessageBuilder msg = new WebhookMessageBuilder();
        msg.setAvatarUrl("https://crafatar.com/avatars/" + e.getPlayer().getUniqueId().toString());
        msg.setUsername(e.getPlayer().getName());
        msg.setContent(e.getMessage());
        client.send(msg.build());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("enable_join_leave_messages", true))
            sendEmbed(e);
        DiscordSync.init.setStatus();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("enable_join_leave_messages", true))
            sendEmbed(e);
        DiscordSync.init.getServer().getScheduler().scheduleAsyncDelayedTask(DiscordSync.init, () -> {
           DiscordSync.init.setStatus();
        }, 60L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("enable_death_messages", true))
            sendDeathEmbed(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKickOrBan(PlayerKickEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("enable_kickban_messages")) {
            if(!e.getReason().contains("restarting"))
                sendEmbed(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAfk(AfkStatusChangeEvent e) {
        if(DiscordSync.init.getConfig().getBoolean("enable_afk_messages", true)) {
            String avatarURL = "https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png";
            if (DiscordSync.init.getConfig().getString("avatar-player-afk", null) != null && !DiscordSync.init.getConfig().getString("avatar-player-afk").isEmpty())
                avatarURL = DiscordSync.init.getConfig().getString("avatar-player-afk");
            WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            WebhookEmbedBuilder web = new WebhookEmbedBuilder();
            web.setTitle(new WebhookEmbed.EmbedTitle(e.getAffected().getName() + (e.getValue() ? " is now AFK" : "is no longer AFK"), null));
            web.setColor(Color.BLACK.getRGB());
            msg.setAvatarUrl(avatarURL);
            msg.setUsername(DiscordSync.init.getConfig().getString("global_message_username", "Server"));
            msg.addEmbeds(web.build());
            client.send(msg.build());
        }
    }

    private void sendEmbed(PlayerEvent e) {
        String avatarURL = "https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png";
        if(e instanceof PlayerJoinEvent && DiscordSync.init.getConfig().getString("avatar-player-died", null) != null && !DiscordSync.init.getConfig().getString("avatar-player-joined").isEmpty())
            avatarURL = DiscordSync.init.getConfig().getString("avatar-player-joined");
        else if(e instanceof PlayerQuitEvent && DiscordSync.init.getConfig().getString("avatar-player-left", null) != null && !DiscordSync.init.getConfig().getString("avatar-player-left").isEmpty())
            avatarURL = DiscordSync.init.getConfig().getString("avatar-player-left");
        else if(e instanceof PlayerKickEvent && DiscordSync.init.getConfig().getString("avatar-player-kickbanned", null) != null && !DiscordSync.init.getConfig().getString("avatar-player-kickbanned").isEmpty())
            avatarURL = DiscordSync.init.getConfig().getString("avatar-player-kickbanned");
        WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
        WebhookMessageBuilder msg = new WebhookMessageBuilder();
        WebhookEmbedBuilder web = new WebhookEmbedBuilder();
        if(e instanceof PlayerJoinEvent || e instanceof PlayerQuitEvent) {
            web.setTitle(new WebhookEmbed.EmbedTitle(e.getPlayer().getName() + ((e instanceof PlayerJoinEvent) ? " has joined " : " has left ") + "the server!", null));
            web.setColor((e instanceof PlayerJoinEvent) ? Color.GREEN.getRGB() : Color.RED.getRGB());
        }
        else if(e instanceof PlayerKickEvent) {
            web.setTitle(new WebhookEmbed.EmbedTitle(e.getPlayer().getName() + " was kicked for: " +  ((PlayerKickEvent) e).getReason(), null));
            web.setColor(Color.MAGENTA.getRGB());
        }
        msg.setAvatarUrl(avatarURL);
        msg.setUsername(DiscordSync.init.getConfig().getString("global_message_username", "Server"));
        msg.addEmbeds(web.build());
        client.send(msg.build());
    }

    private void sendDeathEmbed(PlayerDeathEvent e) {
        String avatarURL = (DiscordSync.init.getConfig().getString("avatar-player-died", null) != null && !DiscordSync.init.getConfig().getString("avatar-player-died").isEmpty() ? DiscordSync.init.getConfig().getString("avatar-player-died") : "https://www.emoji.com/wp-content/uploads/filebase/icons/emoji-icon-glossy-00-05-faces-face-fantasy-skull-and-crossbones-72dpi-forPersonalUseOnly.png");
        WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
        WebhookMessageBuilder msg = new WebhookMessageBuilder();
        WebhookEmbedBuilder web = new WebhookEmbedBuilder();
        web.setTitle(new WebhookEmbed.EmbedTitle(e.getDeathMessage(), null));
        web.setColor(Color.DARK_GRAY.getRGB());
        msg.setAvatarUrl(avatarURL);
        msg.setUsername(DiscordSync.init.getConfig().getString("global_message_username", "Server"));
        msg.addEmbeds(web.build());
        client.send(msg.build());


        /*ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(IOUtils.toByteArray(getClass().getResourceAsStream("/skull-icon.png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(e.getDeathMessage())
                .setColor(Color.DARK_GRAY)
                .setThumbnail("attachment://death.png");
        DiscordSync.channel.sendFile(bais, "death.png").embed(eb.build()).queue();*/
    }

    /*private Image resolveUserFace(Player p) {
        URL imageLoc = null;
        BufferedImage outputImage = null;
        try {
            imageLoc = new URL("https://crafatar.com/avatars/" + p.getUniqueId().toString());
            InputStream imgInput = imageLoc.openStream();
            outputImage = ImageIO.read(imgInput);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputImage;
    }*/
}
