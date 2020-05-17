package com.scorchedcode.wolfplzz.DiscordSync;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DarkUtil {

    private DarkUtil() {

    }

    public static String getURL(String input) {
        Pattern p = Pattern.compile("https?:\\/\\/.+\\.[\\S&&[^,]]+");
        Matcher m = p.matcher(input);
        return (m.find() ? m.group().trim() : input);
    }
}
