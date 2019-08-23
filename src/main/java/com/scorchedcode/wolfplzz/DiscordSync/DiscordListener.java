package com.scorchedcode.wolfplzz.DiscordSync;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.scorchedcode.wolfplzz.Casino.Blackjack;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
                TextComponent prefix = new TextComponent(ChatColor.translateAlternateColorCodes('&', DiscordSync.init.getConfig().getString("relay-prefix", "[&9Discord&f]")));
                if(!event.isWebhookMessage())
                    msg.setText(ChatColor.translateAlternateColorCodes('&', DiscordSync.init.getConfig().getString("minecraft_message_format", ChatColor.RESET + "<{user}> {message}").replaceAll("\\{user}", event.getMember().getEffectiveName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
                else {
                    if(!event.getAuthor().getName().contains("Blackjack") && !event.getAuthor().getName().equals(DiscordSync.init.getConfig().getString("global_message_username")) && Bukkit.getPlayer(event.getAuthor().getName()) == null)
                        msg.setText(ChatColor.translateAlternateColorCodes('&', DiscordSync.init.getConfig().getString("minecraft_message_format", ChatColor.RESET + "<{user}> {message}").replaceAll("\\{user}", event.getAuthor().getName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
                    else
                        return;
                }
                prefix.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DiscordSync.inviteURL));
                prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Join Discord!")}));;
                for(Player p : DiscordSync.init.getServer().getOnlinePlayers())
                    p.spigot().sendMessage(prefix, msg);
                //DiscordSync.init.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', DiscordSync.init.getConfig().getString("minecraft_message_format", ChatColor.DARK_BLUE + "[Discord] " + ChatColor.RESET + "<{user}> {message}").replaceAll("\\{user}", event.getMember().getEffectiveName()).replaceAll("\\{message}", event.getMessage().getContentRaw())));
            }
        }
        if(event.getMessage().getContentRaw().contains("!casinostats") && event.getMember().getRoles().contains(event.getJDA().getRolesByName(DiscordSync.init.getConfig().getString("mod-role"), true).get(0))) {
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
        }
    }
}
