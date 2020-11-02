package nirusu.nirubot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import nirusu.nirubot.Nirubot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(@Nonnull final Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public synchronized void loadAndPlay(@Nonnull final GuildMessageReceivedEvent ctx, @Nonnull  String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(ctx.getGuild());

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        musicManager.setVolume(gm.volume());


        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@Nonnull AudioTrack track) {
                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(@Nonnull final AudioPlaylist playlist) {

                if (playlist.getTracks().size() == 0) return;

                for (AudioTrack t : playlist.getTracks()) {
                    play(musicManager, t);
                }
            }

            @Override
            public void noMatches() {
                ctx.getChannel().sendMessage("No matches found!").queue();;
                throw new IllegalArgumentException();
            }

            @Override
            public void loadFailed(@Nonnull FriendlyException exception) {
                ctx.getChannel().sendMessage("Couldn't load song").queue();
                Nirubot.warning("Couldn't load song!");
                throw new IllegalArgumentException();
            }
        });

        ctx.getGuild().getAudioManager().openAudioConnection(DiscordUtil.findVoiceChannel(ctx.getMember()));
    }

    private synchronized void play(@Nonnull final GuildMusicManager musicManager, @Nonnull final AudioTrack track) {
        musicManager.getScheduler().queue(track);
    }

    public synchronized void pause(@Nonnull final GuildMusicManager musicManager, final boolean pause) {
        musicManager.getPlayer().setPaused(pause);
    }

    public synchronized void destroy(@Nonnull final long guild) {
        GuildMusicManager mg = musicManagers.get(guild);
        // destroy if exists
        if (mg != null) {
            mg.getPlayer().destroy();
        }
        musicManagers.remove(guild);
    }

    public synchronized void next(@Nonnull final GuildMusicManager musicManager) {
        musicManager.getScheduler().nextTrack();
    }

    public synchronized void shuffle(@Nonnull final GuildMusicManager musicManager) {
        musicManager.getScheduler().shuffle();

    }

    public synchronized AudioTrack remove(@Nonnull final GuildMusicManager musicManager, final int num) {
        return musicManager.getScheduler().remove(num);
    }

    public synchronized AudioTrack remove(@Nonnull final GuildMusicManager musicManager, final String keyWord) {
        return musicManager.getScheduler().remove(keyWord);
    }

    public synchronized boolean repeat(@Nonnull final GuildMusicManager musicManager) {
        return musicManager.getScheduler().setRepeat();
    }

    public AudioTrack getPlaying(@Nonnull final GuildMusicManager musicManager) {
        return musicManager.getPlayer().getPlayingTrack();
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

}

