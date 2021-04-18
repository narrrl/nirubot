package nirusu.nirubot.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ActivityUpdateRequest;
import nirusu.nirucmd.CommandContext;

public class DiscordUtil {

    private DiscordUtil() {
        throw new IllegalAccessError();
    }

    public static String formatTime(final long minutes, final long seconds) {
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
            } else if (minutes < 10) {
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

    public static void setActivity(CommandContext ctx, ActivityUpdateRequest req) {
        ctx.getEvent().getClient().updatePresence(Presence.online(req)).blockOptional();

    }

    public static Optional<ActivityUpdateRequest> getActivityUpdateRequest(List<String> args) {
        if (args.size() < 2) {
            return Optional.empty();
        }
        String requestType = args.get(0);
        String activityMessage = String.join(" ", args).replace(requestType + " ", "");

        return switch (requestType) {
            case "playing" -> 
                Optional.of(Activity.playing(activityMessage));
            case "competing" -> 
                Optional.of(Activity.competing(activityMessage));
            case "listening" -> 
                Optional.of(Activity.listening(activityMessage));
            case "watching" -> 
                Optional.of(Activity.watching(activityMessage));
            case "streaming" -> 
                Optional.of(Activity.streaming(activityMessage.replace(args.get(args.size() - 1), ""),
                        args.get(args.size() - 1)));
            default -> 
                Optional.empty();
        };
    }
}
