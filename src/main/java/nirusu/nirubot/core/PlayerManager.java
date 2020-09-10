package nirusu.nirubot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
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


    public synchronized void loadAndPlay(@Nonnull final CommandContext ctx, @Nonnull  String trackUrl, EmbedBuilder emb) {
        GuildMusicManager musicManager = getGuildMusicManager(ctx.getGuild());

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        musicManager.setVolume(gm.volume());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@Nonnull AudioTrack track) {
                play(musicManager, track);
                if (emb == null) {
                    ctx.reply(new EmbedBuilder().setTitle(track.getInfo().title, track.getInfo().uri)
                    .setThumbnail(ctx.getGuild().getIconUrl()).build());
                } else {
                    ctx.reply(emb.build());
                }
            }

            @Override
            public void playlistLoaded(@Nonnull final AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> play(musicManager, track));
                if (emb == null) {
                    AudioTrack first = playlist.getTracks().get(0);
                    String firstText = first != null ? "Loaded playlist! First Track is: \n[" 
                    + first.getInfo().title + "](" + first.getInfo().uri + ")" : "No tracks in playlist!";
                    ctx.reply(new EmbedBuilder().setTitle(playlist.getName()).setDescription(firstText)
                    .setThumbnail(ctx.getGuild().getIconUrl()).build());
                } else {
                    ctx.reply(emb.build());
                }
            }

            @Override
            public void noMatches() {
                ctx.reply("Couldn't find: " + trackUrl);
            }

            @Override
            public void loadFailed(@Nonnull FriendlyException exception) {
                Nirubot.warning(exception.getMessage());
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

    public synchronized void destroy(@Nonnull final Guild guild) {
        musicManagers.get(guild.getIdLong()).getPlayer().destroy();
        musicManagers.remove(guild.getIdLong());
    }

    public void next(@Nonnull final GuildMusicManager musicManager) {
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
}
