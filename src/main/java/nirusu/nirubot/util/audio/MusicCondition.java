package nirusu.nirubot.util.audio;

import java.util.Optional;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import nirusu.nirucmd.CommandContext;
import nirusu.nirubot.core.audio.GuildMusicManager;

public enum MusicCondition {
    USER_CONNECTED("You must be in a voice channel!") {
        @Override
        public boolean check(CommandContext ctx) {
            return ctx.getAuthorVoiceState().flatMap(user -> user.getChannel().blockOptional()).isPresent();
        }
    },
    SAME_VOICE_CHANNEL("You must be in the same voice channel as the bot!") {
        @Override
        public boolean check(CommandContext ctx) {
            Optional<VoiceState> botState = ctx.getSelfVoiceState();
            Optional<VoiceState> userState = ctx.getAuthorVoiceState();
            return botState
                    .map(bs -> bs.getChannel().blockOptional()
                            .map(ch -> ch.isMemberConnected(ctx.getMember().map(Member::getId).orElse(null))
                                    .blockOptional().orElse(false))
                            .orElse(true))
                    .orElse(userState.map(us -> us.getChannel().blockOptional().isPresent()).orElse(false));
        }
    },
    MUSIC_PLAYING("No music is playing!") {
        @Override
        public boolean check(CommandContext ctx) {
            Optional<AudioTrack> t = ctx.getGuild().flatMap(guild -> {
                GuildMusicManager manager = GuildMusicManager.of(guild.getId());
                final AudioTrack track = manager.getPlayer().getPlayingTrack();
                return Optional.ofNullable(track);
            });
            return t.isPresent();
        }
    },
    BOT_CONNECTED("Bot is not connected!") {
        @Override
        public boolean check(CommandContext ctx) {
            Optional<VoiceChannel> ch = ctx.getSelfVoiceState().flatMap(state -> state.getChannel().blockOptional());
            return ch.isPresent();
        }
    };

    private final String errorMessage;

    private MusicCondition(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public abstract boolean check(final CommandContext ctx);

    public static boolean checkConditions(final CommandContext ctx, MusicCondition... conditions) {

        for (MusicCondition condition : conditions) {
            if (!condition.check(ctx)) {
                ctx.reply(condition.getErrorMessage());
                return false;
            }
        }

        return true;

    }

}
