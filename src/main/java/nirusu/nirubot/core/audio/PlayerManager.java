package nirusu.nirubot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.Guild;
import discord4j.rest.util.Color;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirucmd.CommandContext;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all the music for the bot.
 *
 */
public class PlayerManager {
    private static PlayerManager instance;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(@Nonnull final Guild guild) {
        long guildId = guild.getId().asLong();
        return musicManagers.computeIfAbsent(guildId, id -> {
            GuildMusicManager musicManager;
            musicManager = new GuildMusicManager(audioPlayerManager);
            musicManagers.put(guildId, musicManager);
            musicManager.setVolume(GuildManager.getManager(guild.getId().asLong()).volume());
            return musicManager;
        });
    }

    public synchronized void loadAndPlay(@Nonnull final CommandContext ctx, @Nonnull  String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(ctx.getGuild().orElseThrow(IllegalArgumentException::new));

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@Nonnull AudioTrack track) {
                play(musicManager, track);
                ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec -> 
                    spec.setTitle("Song loaded: " + track.getInfo().title)
                        .setUrl(track.getInfo().uri)
                        .setColor(Color.of(Nirubot.getColor().getRGB())
                )).block());
            }

            @Override
            public void playlistLoaded(@Nonnull final AudioPlaylist playlist) {

                if (playlist.getTracks().isEmpty()) return;

                for (AudioTrack t : playlist.getTracks()) {
                    play(musicManager, t);
                }
                ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec -> 
                    spec.setTitle("Playlist loaded: " + playlist.getName())
                        .setColor(Color.of(Nirubot.getColor().getRGB())
                )).block());
            }

            @Override
            public void noMatches() {
                ctx.reply("Nothing found by " + trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                ctx.reply("Couldn't load song!");
            }
        });
    }

    private synchronized void play(@Nonnull final GuildMusicManager guildMusicManager, @Nonnull final AudioTrack track) {
        guildMusicManager.getScheduler().queue(track);
    }

    public synchronized void pause(@Nonnull final Guild guild, final boolean pause) {
        getGuildMusicManager(guild).getPlayer().setPaused(pause);
    }

    public synchronized void destroy(@Nonnull final long guild) {
        GuildMusicManager mg = musicManagers.get(guild);
        // destroy if exists
        if (mg != null) {
            mg.getPlayer().destroy();
        }
        musicManagers.remove(guild);
    }

    public synchronized void next(@Nonnull final Guild guild) {
        getGuildMusicManager(guild).getScheduler().nextTrack();
    }

    public synchronized void shuffle(@Nonnull final Guild guild) {
        getGuildMusicManager(guild).getScheduler().shuffle();

    }

    public synchronized AudioTrack remove(@Nonnull final Guild guild, final int num) {
        return getGuildMusicManager(guild).getScheduler().remove(num);
    }

    public synchronized AudioTrack remove(@Nonnull final Guild guild, final String keyWord) {
        return getGuildMusicManager(guild).getScheduler().remove(keyWord);
    }

    public synchronized boolean repeat(@Nonnull final Guild guild) {
        return getGuildMusicManager(guild).getScheduler().setRepeat();
    }

    public AudioTrack getPlaying(@Nonnull final Guild guild) {
        return getGuildMusicManager(guild).getPlayer().getPlayingTrack();
    }

    public static synchronized PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }

        return instance;
    }

    public synchronized void shutdown() {
        for (long l : musicManagers.keySet()) {
            destroy(l);
        }
    }

	public String[] getCurrentSongs(Guild guild) {
        GuildMusicManager mg = getGuildMusicManager(guild);
        ArrayList<AudioTrackInfo> tracks = mg.getScheduler().getAllTrackInfos();
        String[] uris = new String[tracks.size()];

        for (int i = 0; i < tracks.size(); i++ ) {
            uris[i] = tracks.get(i).uri;
        }
        return uris;
    }

	public Collection<GuildMusicManager> getAllManager() {
		return musicManagers.values();
    }

    public Collection<Long> getAllIds() {
        return musicManagers.keySet();
    }
}

