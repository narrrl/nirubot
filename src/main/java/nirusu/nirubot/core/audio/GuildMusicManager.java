package nirusu.nirubot.core.audio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.common.util.Snowflake;
import nirusu.nirucmd.CommandContext;

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

    private GuildMusicManager() {
        player = PLAYER_MANAGER.createPlayer();
        scheduler = new TrackScheduler(player);
        provider = new D4jAudioProvider(player);

        player.addListener(scheduler);
    }

    /**
     * Creates a player and a track scheduler.
     * 
     * @param manager Audio player manager to use for creating the player.
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

    public void loadAndPlay(String link, CommandContext ctx) {
        ResultHandler handler = new ResultHandler.Builder(this).setCTX(ctx).build();
        PLAYER_MANAGER.loadItemOrdered(this, link, handler);
    }

    public void loadAndPlayNext(String link, CommandContext ctx) {
        ResultHandler handler = new ResultHandler.Builder(this).setCTX(ctx).setPlayAsNext(true).build();
        PLAYER_MANAGER.loadItemOrdered(this, link, handler);
    }

    public static void destroy(Snowflake id) {
        GuildMusicManager manager = MANAGERS.remove(id);
        manager.player.destroy();
    }
}
