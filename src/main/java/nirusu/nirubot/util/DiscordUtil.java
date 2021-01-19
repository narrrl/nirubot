package nirusu.nirubot.util;

import java.util.function.Consumer;

import discord4j.core.spec.EmbedCreateSpec;
import nirusu.nirucmd.CommandContext;

public class DiscordUtil {


    private DiscordUtil() { throw new IllegalAccessError(); }


    public static String formatTime(final long minutes , final long seconds) {
        StringBuilder out = new StringBuilder();

        if (minutes > 60) {

            long hours = minutes / 60;

            if (hours < 10) {
                out.append("0").append(hours);
            } else {
                out.append(hours);
            }

            out.append(":");

            long min = minutes % 60;

            if (min < 10) {
                out.append("0").append(min);
            } else {
                out.append(min);
            }
        } else {
            if (minutes == 0) {
                out.append("0");
            }
            else if (minutes < 10) {
                out.append("0").append(minutes);
            } else {
                out.append(minutes);
            }
        }

        out.append(":");

        if (seconds < 10) {
            out.append("0").append(seconds);
        } else {
            out.append(seconds);
        }

        return out.toString();
    }

    public static void sendEmbed(CommandContext ctx, Consumer<? super EmbedCreateSpec> spec) {
        ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec).block());
    }
}
