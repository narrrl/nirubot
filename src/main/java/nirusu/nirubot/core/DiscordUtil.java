package nirusu.nirubot.core;

import javax.annotation.Nonnull;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;

public class DiscordUtil {


    private DiscordUtil() { throw new IllegalAccessError(); }

    public static VoiceChannel findVoiceChannel(@Nonnull Member member) {
        VoiceState state = member.getVoiceState().block();
        return state != null ? state.getChannel().block() : null;
    }

    /**
     * Checks if both member are in the same voice channel
     */
	public static boolean areInSameVoice(Member member, Member selfMember) {
        VoiceChannel channel = findVoiceChannel(member);

        if (channel == null) return false;

        VoiceChannel botChannel = findVoiceChannel(selfMember);

        if (botChannel == null) return true;


        return !channel.equals(botChannel);
    }

        public static String formatTime(final long minutes , final long seconds) {
        StringBuilder out = new StringBuilder();

        if (minutes > 60) {

            long hours = minutes / 60;

            if (hours < 10) {
                out.append("0" + hours);
            } else {
                out.append(hours);
            }

            out.append(":");

            long min = minutes % 60;

            if (min < 10) {
                out.append("0" + min);
            } else {
                out.append(min);
            }
        } else {
            if (minutes == 0) {
                out.append("0");
            }
            else if (minutes < 10) {
                out.append("0" + minutes);
            } else {
                out.append(minutes);
            }
        }

        out.append(":");

        if (seconds < 10) {
            out.append("0" + seconds);
        } else {
            out.append(seconds);
        }

        return out.toString();
    }
}
