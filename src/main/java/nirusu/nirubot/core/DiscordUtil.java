package nirusu.nirubot.core;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nirusu.nirubot.command.CommandContext;

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

    public static VoiceChannel findVoiceChannel(@Nonnull Member member) {
        GuildVoiceState state = member.getVoiceState();
        return state != null ? state.getChannel() : null;
    }

    /**
     * Checks if both member are in the same voice channel
     */
	public static boolean areInSameVoice(Member member, Member selfMember) {
        VoiceChannel channel = DiscordUtil.findVoiceChannel(member);

        if (channel == null) return false;

        VoiceChannel botChannel = DiscordUtil.findVoiceChannel(selfMember);

        if (!channel.equals(botChannel)) return false;

        return true;
    }
    
    public static void play(@Nonnull final String url, @Nonnull final CommandContext ctx) {
        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(ctx, url);

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        manager.getGuildMusicManager(ctx.getGuild()).getPlayer()
                .setVolume(gm.volume());

        ctx.getGuild().getAudioManager().openAudioConnection(DiscordUtil.findVoiceChannel(ctx.getMember()));
    }
    
}
