package com.scorchedcode.DiscordSync;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.mineacademy.fo.settings.SimpleSettings;

public class Settings extends SimpleSettings {

    public static String TOKEN;
    public static String MC_ROOM_NAME;
    public static String HELPOP_ROOM;
    public static String MAINTENANCE_STATUS;
    public static String CUSTOM_STATUS;
    public static String RELAY_PREFIX;
    public static String MINECRAFT_MESSAGE_FORMAT;
    public static String GLOBAL_MESSAGE_USERNAME;
    public static String MOD_ROLE;
    public static String SERVER_START_MESSAGE;
    public static String SERVER_RESTART_MESSAGE;
    public static Boolean DISABLE_MENTiON_ALL;
    public static Boolean ENABLE_JOIN_LEAVE_MESSAGES;
    public static Boolean ENABLE_DEATH_MESSAGES;
    public static Boolean ENABLE_KICKBAN_MESSAGES;
    public static Boolean ENABLE_AFK_MESSAGES;
    public static String AVATAR_PLAYER_AFK;
    public static String AVATAR_PLAYER_DIED;
    public static String AVATAR_PLAYER_JOINED;
    public static String AVATAR_PLAYER_LEFT;
    public static String AVATAR_PLAYER_KICKBANNED;
    public static String SERVER_DOMAIN;

    @Override
    protected int getConfigVersion() {
        return 2;
    }

    private static void init() {
        TOKEN = getConfig().getString("token", null);
        MC_ROOM_NAME = getConfig().getString("mc-room-name", "");
        HELPOP_ROOM = getConfig().getString("helpop-room");
        MAINTENANCE_STATUS = getConfig().getString("maintenance-status", "Subscribe!");
        CUSTOM_STATUS = getConfig().getString("custom-status", "Subscribe!");
        RELAY_PREFIX = getConfig().getString("relay-prefix", "[&9Discord&f]");
        MINECRAFT_MESSAGE_FORMAT = getConfig().getString("minecraft_message_format", ChatColor.RESET + "<{user}> {message}");
        GLOBAL_MESSAGE_USERNAME = getConfig().getString("global_message_username");
        MOD_ROLE = getConfig().getString("mod-role");
        SERVER_START_MESSAGE = getConfig().getString("server-start-message", null);
        SERVER_RESTART_MESSAGE = getConfig().getString("server-restart-message", null);
        DISABLE_MENTiON_ALL = getConfig().getBoolean("disable_mention_all", false);
        ENABLE_JOIN_LEAVE_MESSAGES = getConfig().getBoolean("enable_join_leave_messages", true);
        ENABLE_DEATH_MESSAGES = getConfig().getBoolean("enable_death_messages", true);
        ENABLE_KICKBAN_MESSAGES = getConfig().getBoolean("enable_kickban_messages");
        ENABLE_AFK_MESSAGES = getConfig().getBoolean("enable_afk_messages", true);
        AVATAR_PLAYER_AFK = getConfig().getString("avatar-player-afk", null);
        AVATAR_PLAYER_DIED = getConfig().getString("avatar-player-died", null);
        AVATAR_PLAYER_JOINED = getConfig().getString("avatar-player-joined", null);
        AVATAR_PLAYER_LEFT = getConfig().getString("avatar-player-left", null);
        AVATAR_PLAYER_KICKBANNED = getConfig().getString("avatar-player-kickbanned", null);
        SERVER_DOMAIN = getConfig().getString("server-domain", Bukkit.getIp());
    }
}
