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
        VoiceChannel channel = DiscordUtil.findVoiceChannel(member);

        if (channel == null) return false;

        VoiceChannel botChannel = DiscordUtil.findVoiceChannel(selfMember);

        if (botChannel == null) return true;


        if (!channel.equals(botChannel)) return false;

        return true;
    }

    
}
