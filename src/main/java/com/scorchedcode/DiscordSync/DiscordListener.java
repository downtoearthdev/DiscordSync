package com.scorchedcode.DiscordSync;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;

import javax.annotation.Nonnull;
import java.awt.*;

public class DiscordListener extends ListenerAdapter {

    public DiscordListener() {

    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getChannel().equals(DiscordSync.channel)) {
            if(!event.getAuthor().getName().equals(event.getJDA().getSelfUser().getName()) && event.getMessage().getContentRaw().indexOf("!") != 0) {
                TextComponent msg = new TextComponent();
                TextComponent msgtwo = new TextComponent();
                TextComponent url = new TextComponent();
                TextComponent prefix = new TextComponent(ChatColor.translateAlternateColorCodes('&', Settings.RELAY_PREFIX));
                if(!event.isWebhookMessage()) {
                    msg.setText(ChatColor.translateAlternateColorCodes('&', Settings.MINECRAFT_MESSAGE_FORMAT.replaceAll("\\{user}", event.getMember().getEffectiveName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
                    //Make links clickable
                    if(DarkUtil.getURL(event.getMessage().getContentRaw()) != null) {
                        String furl = DarkUtil.getURL(event.getMessage().getContentRaw());
                        String[] split = msg.getText().split(furl);
                        msg.setText(split[0]);
                        msgtwo.setText((split.length > 1) ? split[1] : "");
                        url.setText(furl);
                        url.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, furl));
                    }
                }
                else {
                    if(!event.getAuthor().getName().contains("Blackjack") && event.getMessage().getEmbeds().size() == 0 && !event.getAuthor().getName().equals(Settings.GLOBAL_MESSAGE_USERNAME) && Bukkit.getPlayer(event.getAuthor().getName()) == null)
                        msg.setText(ChatColor.translateAlternateColorCodes('&', Settings.MINECRAFT_MESSAGE_FORMAT.replaceAll("\\{user}", event.getAuthor().getName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
                    else
                        return;
                }
                prefix.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DiscordSync.inviteURL));
                prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Join Discord!")}));;
                for(Player p : DiscordSync.getInstance().getServer().getOnlinePlayers())
                    p.spigot().sendMessage(prefix, msg, url, msgtwo);
                //DiscordSync.getInstance().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', DiscordSync.getInstance().getConfig().getString("minecraft_message_format", ChatColor.DARK_BLUE + "[Discord] " + ChatColor.RESET + "<{user}> {message}").replaceAll("\\{user}", event.getMember().getEffectiveName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
            }
        }
        /*if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!casinostats") && event.getMember().getRoles().contains(event.getJDA().getRolesByName(Settings.MOD_ROLE, true).get(0))) {
            if(event.getMessage().getContentRaw().split("").length > 1) {
                String mesg = Blackjack.getStats(event.getMessage().getContentRaw().split(" ")[1]);
                if (!mesg.isEmpty()) {
                    WebhookClient client = new WebhookClientBuilder(DiscordSync.hook.getUrl()).build();
                    WebhookMessageBuilder msg = new WebhookMessageBuilder();
                    WebhookEmbedBuilder web = new WebhookEmbedBuilder();
                    web.setTitle(new WebhookEmbed.EmbedTitle(event.getMessage().getContentRaw().split(" ")[1], null));
                    web.setColor(Color.DARK_GRAY.getRGB());
                    web.setDescription(mesg);
                    msg.setAvatarUrl("https://cdn.pixabay.com/photo/2013/07/12/12/01/suit-of-spades-145116_960_720.png");
                    msg.setUsername("Blackjack Score");
                    msg.addEmbeds(web.build());
                    client.send(msg.build());
                }
            }
        }*/

        if(event.getMessage().getContentRaw().equalsIgnoreCase("!discord") ||
            event.getMessage().getContentRaw().equalsIgnoreCase("!twitch") ||
            event.getMessage().getContentRaw().equalsIgnoreCase("!website") ||
            event.getMessage().getContentRaw().equalsIgnoreCase("!youtube") ||
            event.getMessage().getContentRaw().equalsIgnoreCase("!teamspeak") ||
            event.getMessage().getContentRaw().equalsIgnoreCase("!map"))
                Common.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw().replaceAll("!", ""));

        if(event.getMessage().getContentRaw().equalsIgnoreCase("!playerlist")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor(p.getName(), "https://crafatar.com/avatars/" + p.getUniqueId().toString())
                .setColor((HookManager.isAfk(p)) ? Color.DARK_GRAY : Color.GREEN);
                event.getChannel().sendMessage(builder.build()).complete();
            }
        }

        if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!mcs") && event.getMember().getRoles().contains(event.getJDA().getRolesByName(Settings.MOD_ROLE, true).get(0))) {
               //event.getChannel().sendMessage("TPS: " + DiscordSync.getInstance().getServer(). );
        }
    }
}
