package com.scorchedcode.wolfplzz.DiscordSync;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.scorchedcode.wolfplzz.Fixes.events.MaintenanceEvent;
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
            DiscordSync.getInstance().setStatus();
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
            DiscordSync.getInstance().setStatus();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerStart(ServerLoadEvent e) {
        if(Settings.SERVER_START_MESSAGE != null && !Settings.SERVER_START_MESSAGE.isEmpty()) {
            List<Message> msgs = DiscordSync.channel.getIterableHistory().complete();
            if(msgs.get(0).getEmbeds().size() > 0) {
                if (msgs.get(0).getEmbeds().get(0).getTitle().contains(Settings.SERVER_START_MESSAGE))
                    msgs.get(0).delete().queue();
            }
            WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            WebhookEmbedBuilder web = new WebhookEmbedBuilder();
            web.setTitle(new WebhookEmbed.EmbedTitle(Settings.SERVER_START_MESSAGE, null));
            web.setColor(Color.DARK_GRAY.getRGB());
            msg.setAvatarUrl("https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png");
            msg.setUsername(Settings.GLOBAL_MESSAGE_USERNAME);
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
        if(Settings.DISABLE_MENTiON_ALL && (e.getMessage().contains("@here") || e.getMessage().contains("@everyone"))) {
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
        if(Settings.ENABLE_JOIN_LEAVE_MESSAGES)
            sendEmbed(e);
        DiscordSync.getInstance().setStatus();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent e) {
        if(Settings.ENABLE_JOIN_LEAVE_MESSAGES)
            sendEmbed(e);
        DiscordSync.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(DiscordSync.getInstance(), () -> {
           DiscordSync.getInstance().setStatus();
        }, 60L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(Settings.ENABLE_DEATH_MESSAGES)
            sendDeathEmbed(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKickOrBan(PlayerKickEvent e) {
        if(Settings.ENABLE_KICKBAN_MESSAGES) {
            if(!e.getReason().contains("restarting"))
                sendEmbed(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAfk(AfkStatusChangeEvent e) {
        if(Settings.ENABLE_AFK_MESSAGES) {
            String avatarURL = "https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png";
            if (Settings.AVATAR_PLAYER_AFK != null && !Settings.AVATAR_PLAYER_AFK.isEmpty())
                avatarURL = Settings.AVATAR_PLAYER_AFK;
            WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            WebhookEmbedBuilder web = new WebhookEmbedBuilder();
            web.setTitle(new WebhookEmbed.EmbedTitle(e.getAffected().getName() + (e.getValue() ? " is now AFK" : " is no longer AFK"), null));
            web.setColor(Color.BLACK.getRGB());
            msg.setAvatarUrl(avatarURL);
            msg.setUsername(Settings.GLOBAL_MESSAGE_USERNAME);
            msg.addEmbeds(web.build());
            client.send(msg.build());
        }
    }

    private void sendEmbed(PlayerEvent e) {
        String avatarURL = "https://gamepedia.cursecdn.com/minecraft_gamepedia/4/44/Grass_Block_Revision_6.png";
        if(e instanceof PlayerJoinEvent && Settings.AVATAR_PLAYER_JOINED != null && !Settings.AVATAR_PLAYER_JOINED.isEmpty())
            avatarURL = Settings.AVATAR_PLAYER_JOINED;
        else if(e instanceof PlayerQuitEvent && Settings.AVATAR_PLAYER_LEFT != null && !Settings.AVATAR_PLAYER_LEFT.isEmpty())
            avatarURL = Settings.AVATAR_PLAYER_LEFT;
        else if(e instanceof PlayerKickEvent && Settings.AVATAR_PLAYER_KICKBANNED != null && !Settings.AVATAR_PLAYER_KICKBANNED.isEmpty())
            avatarURL = Settings.AVATAR_PLAYER_KICKBANNED;
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
        msg.setUsername(Settings.GLOBAL_MESSAGE_USERNAME);
        msg.addEmbeds(web.build());
        client.send(msg.build());
    }

    private void sendDeathEmbed(PlayerDeathEvent e) {
        String avatarURL = (Settings.AVATAR_PLAYER_DIED != null && !Settings.AVATAR_PLAYER_DIED.isEmpty() ? Settings.AVATAR_PLAYER_DIED : "https://www.emoji.com/wp-content/uploads/filebase/icons/emoji-icon-glossy-00-05-faces-face-fantasy-skull-and-crossbones-72dpi-forPersonalUseOnly.png");
        WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
        WebhookMessageBuilder msg = new WebhookMessageBuilder();
        WebhookEmbedBuilder web = new WebhookEmbedBuilder();
        web.setTitle(new WebhookEmbed.EmbedTitle(e.getDeathMessage(), null));
        web.setColor(Color.DARK_GRAY.getRGB());
        msg.setAvatarUrl(avatarURL);
        msg.setUsername(Settings.GLOBAL_MESSAGE_USERNAME);
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
