package nirusu.nirubot.core.audio;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import nirusu.nirubot.core.Result;
import nirusu.nirubot.core.Result.ResultType;
import reactor.core.publisher.Mono;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
    private static final Map<Snowflake, GuildMusicManager> MANAGERS = new ConcurrentHashMap<>();
    public static final AudioPlayerManager PLAYER_MANAGER;

    static {
        PLAYER_MANAGER = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize to minimize
        // allocations
        PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
    }

    public static GuildMusicManager of(final Snowflake id) {
        return MANAGERS.computeIfAbsent(id, ignored -> new GuildMusicManager());
    }

    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final D4jAudioProvider provider;
    private VoiceConnection connection;

    private GuildMusicManager() {
        player = PLAYER_MANAGER.createPlayer();
        scheduler = new TrackScheduler(player);
        provider = new D4jAudioProvider(player);

        player.addListener(scheduler);
    }

    public Optional<Result> play(@Nonnull VoiceState authorState, @Nonnull String link,
            @Nonnull ResultHandler handler) {
        VoiceChannel ch = authorState.getChannel().block();

        if (ch == null || (connection != null && !ch.getId().equals(connection.getChannelId().block()))) {

            return Optional.of(new Result("You must be in the same voice channel!", ResultType.FAILURE));

        }

        loadAndPlay(link, handler);
        connection = ch.join(spec -> spec.setProvider(getProvider())).block();

        return Optional.empty();
    }

    public Optional<Result> skip() {

        return Optional.empty();
    }

    public Optional<Result> join(@Nonnull VoiceState state) {

        VoiceChannel ch = state.getChannel().block();

        if (ch == null) {
            return Optional.of(new Result("You must be in a voice channel!", ResultType.FAILURE));
        }

        connection = disconnect().then(ch.join(spec -> spec.setProvider(getProvider()))).block();
        return Optional.empty();
    }

    public Mono<Void> disconnect() {
        if (connection == null)
            return Mono.empty();
        return connection.disconnect();
    }

    /**
     * Creates a player and a track scheduler.
     */
    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public D4jAudioProvider getProvider() {
        return this.provider;
    }

    public void setVolume(final int volume) {
        int real = Math.min(volume, 100);
        real = Math.max(real, 0);
        real = volume < 5 ? 1 : real / 5;
        player.setVolume(real);
    }

    public void loadAndPlay(String link, ResultHandler handler) {
        PLAYER_MANAGER.loadItemOrdered(this, link, handler);
    }

    public void loadAndPlayNext(String link, ResultHandler handler) {
        PLAYER_MANAGER.loadItemOrdered(this, link, handler);
    }

    public static void destroy(Snowflake id) {
        GuildMusicManager manager = MANAGERS.remove(id);
        manager.player.destroy();
    }
}
