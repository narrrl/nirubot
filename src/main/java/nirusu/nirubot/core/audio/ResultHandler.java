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

    private ResultHandler(@NonNull GuildMusicManager manager, CommandContext ctx) {
        this.ctx = Optional.ofNullable(ctx);
        this.manager = manager;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        manager.getScheduler().play(track);
        ctx.ifPresent(c -> c.getChannel()
                .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle("Song loaded: " + track.getInfo().title)
                        .setUrl(track.getInfo().uri).setColor(Nirubot.getColor())).block()));

    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getTracks().isEmpty())
            return;

        for (AudioTrack t : playlist.getTracks()) {
            manager.getScheduler().play(t);
        }
        ctx.ifPresent(c -> c.getChannel()
                .ifPresent(ch -> ch.createEmbed(
                        spec -> spec.setTitle("Playlist loaded: " + playlist.getName()).setColor(Nirubot.getColor()))
                        .block()));
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

        public Builder(@NonNull GuildMusicManager manager) {
            this.manager = manager;
        }

        public Builder setCTX(CommandContext ctx) {
            this.ctx = ctx;
            return this;
        }

        public ResultHandler build() {
            return new ResultHandler(manager, ctx);
        }

    }

}
