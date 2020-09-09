package nirusu.nirubot.core;

import net.dv8tion.jda.api.entities.Activity;

public class DiscordUtil {

    private DiscordUtil() { throw new IllegalAccessError(); }

    public static Activity getActivity(final String type, final String msg) {
        Activity act;
        switch (type) {
            case "playing" -> act = net.dv8tion.jda.api.entities.Activity.playing(msg);
            case "watching" -> act = net.dv8tion.jda.api.entities.Activity.watching(msg);
            case "listening" -> act = net.dv8tion.jda.api.entities.Activity.listening(msg);
            default -> throw new IllegalArgumentException("invalid activity type");
        }
        return act;
    }
    
}
