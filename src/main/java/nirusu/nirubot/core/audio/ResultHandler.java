package nirusu.nirubot.core.audio;

import java.util.Optional;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import nirusu.nirubot.Nirubot;
import nirusu.nirucmd.CommandContext;
import reactor.util.annotation.NonNull;

public class ResultHandler implements AudioLoadResultHandler {
    private final Optional<CommandContext> ctx;
    private final GuildMusicManager manager;
    private final boolean playSongAsNext;

    private ResultHandler(@NonNull GuildMusicManager manager, CommandContext ctx, boolean playSongAsNext) {
        this.ctx = Optional.ofNullable(ctx);
        this.manager = manager;
        this.playSongAsNext = playSongAsNext;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (playSongAsNext) {
            manager.getScheduler().playNext(track);
        } else {
            manager.getScheduler().play(track);
        }
        ctx.flatMap(CommandContext::getChannel)
                .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle("Song loaded: " + track.getInfo().title)
                        .setUrl(track.getInfo().uri).setColor(Nirubot.getColor())).block());

    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getTracks().isEmpty())
            return;

        for (AudioTrack t : playlist.getTracks()) {
            if (playSongAsNext) {
                manager.getScheduler().playNext(t);
            } else {
                manager.getScheduler().play(t);
            }
        }
        ctx.flatMap(CommandContext::getChannel)
                .ifPresent(ch -> ch.createEmbed(
                        spec -> spec.setTitle("Playlist loaded: " + playlist.getName()).setColor(Nirubot.getColor()))
                        .block());
    }

    @Override
    public void noMatches() {
        ctx.ifPresent(c -> c.reply("Nothing found!"));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        ctx.ifPresent(c -> c.reply("Couldn't load song! " + exception.getMessage()));
    }

    public static final class Builder {
        private final GuildMusicManager manager;
        private CommandContext ctx = null;
        private boolean playSongAsNext = false;

        public Builder(@NonNull GuildMusicManager manager) {
            this.manager = manager;
        }

        public Builder setCTX(CommandContext ctx) {
            this.ctx = ctx;
            return this;
        }

        public Builder setPlayAsNext(boolean b) {
            this.playSongAsNext = b;
            return this;
        }

        public ResultHandler build() {
            return new ResultHandler(manager, ctx, playSongAsNext);
        }

    }

}
