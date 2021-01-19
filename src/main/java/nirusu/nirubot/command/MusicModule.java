package nirusu.nirubot.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.channel.Channel.Type;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.audio.GuildMusicManager;
import nirusu.nirubot.util.DiscordUtil;
import nirusu.nirubot.util.audio.MusicCondition;
import nirusu.nirubot.util.YouTubeVideo;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class MusicModule extends BaseModule {
    private static final String NEXT_ARGUMENT = "-next";

    @Command(key = { "p", "play", "pl" }, description = "Plays a song", context = { Type.GUILD_CATEGORY,
            Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void play() {
        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED)) {
            return;
        }
        ctx.getGuild().ifPresent(guild -> ctx.getArgs().ifPresent(args -> {
            String link;
            GuildMusicManager musicManager = GuildMusicManager.of(guild.getId());

            if (args.size() == 1) {
                link = args.get(0);
                ctx.getAuthorVoiceState().flatMap(state -> state.getChannel().blockOptional()).ifPresent(ch -> {
                    musicManager.loadAndPlay(link, ctx);
                    ch.join(con -> con.setProvider(musicManager.getProvider())).block();
                });
            } else if (args.size() == 2 && args.get(0).equals(NEXT_ARGUMENT)) {
                link = args.get(1);
                ctx.getAuthorVoiceState().flatMap(state -> state.getChannel().blockOptional()).ifPresent(ch -> {
                    musicManager.loadAndPlayNext(link, ctx);
                    ch.join(con -> con.setProvider(musicManager.getProvider())).block();
                });
            }

        }));

    }

    @Command(key = { "skip", "next", "s", "sk" }, description = "Skips the current song", context = {
            Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void skip() {

        if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL, MusicCondition.MUSIC_PLAYING)) {
            return;
        }

        ctx.getGuild().ifPresent(guild -> ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty()) {
                return;
            }
            GuildMusicManager musicManager = GuildMusicManager.of(guild.getId());

            AudioTrack prev = musicManager.getPlayer().getPlayingTrack();

            musicManager.getScheduler().skip();

            AudioTrack next = musicManager.getPlayer().getPlayingTrack();
            String prevText = "[" + prev.getInfo().title + "](" + prev.getInfo().uri + ")";
            String nextText = next != null ? "[" + next.getInfo().title + "](" + next.getInfo().uri + ")"
                    : "End of queue!";
            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setColor(Nirubot.getColor()).setTitle("Skipped Song!")
                            .addField("Skipped:", prevText, true).addField("Next:", nextText, true)).block());

        }));
    }

    @Command(key = { "vol", "vl", "volume" }, description = "Sets the volume for the bot")
    public void volume() {

        if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }

        ctx.getArgs().ifPresent(args -> ctx.getGuild().ifPresent(guild -> {
            if (args.size() != 1) {
                return;
            }

            GuildMusicManager musicManager = GuildMusicManager.of(guild.getId());
            int volume;
            try {
                volume = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                ctx.reply(String.format("%s is not a valid volume", args.get(0)));
                return;
            }

            if (volume < 0)
                return;

            volume = Math.min(volume, 100);

            musicManager.setVolume(volume);
            GuildManager.of(guild.getId()).setVolume(volume);

            final String volumeBar = getVolumeBar(volume);
            ctx.getChannel().ifPresent(
                    ch -> ch.createEmbed(spec -> spec.setColor(Nirubot.getColor()).setDescription(volumeBar)).block());
        }));
    }

    @Command(key = { "join", "j" }, description = "Joins into the channel of the author", context = {
            Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void join() {
        ctx.getGuild().ifPresent(guild -> {
            int argsSize = ctx.getArgs().map(List::size).orElse(-1);

            if (argsSize != 0) {
                return;
            }

            if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED)) {
                return;
            }
            GuildMusicManager manager = GuildMusicManager.of(guild.getId());
            disconnectChannel();
            joinChannel(manager);

        });
    }

    @Command(key = { "leave", "left", "l" }, description = "Leaves the current voice channel", context = {
            Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void leave() {
        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED, MusicCondition.BOT_CONNECTED,
                MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }

        ctx.getGuild().ifPresent(guild -> ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty()) {
                return;
            }

            disconnectChannel();
            GuildMusicManager.destroy(guild.getId());
        }));
    }

    @Command(key = { "repeat", "loop", "rp" }, description = "Repeats the current playlist", context = {
            Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void repeat() {
        if (!MusicCondition.checkConditions(ctx, MusicCondition.BOT_CONNECTED, MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }

        ctx.getArgs().ifPresent(args -> ctx.getGuild().ifPresent(guild -> {
            if (!args.isEmpty()) {
                return;
            }

            if (GuildMusicManager.of(guild.getId()).getScheduler().setRepeat()) {
                ctx.reply("Now repeating current playlist!");
            } else {
                ctx.reply("Stopped repeating playlist!");
            }

        }));

    }

    @Command(key = { "pause", "resume" }, description = "Pause/Resume music", context = { Type.GUILD_CATEGORY,
            Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void pause() {
        ctx.getArgs().ifPresent(args -> ctx.getGuild().ifPresent(guild -> {
            if (!args.isEmpty()) {
                return;
            }

            if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL, MusicCondition.MUSIC_PLAYING)) {
                return;
            }

            GuildMusicManager musicManager = GuildMusicManager.of(guild.getId());

            musicManager.getPlayer().setPaused(!musicManager.getPlayer().isPaused());

        }));
    }

    @Command(key = { "playing", "nowplaying", "np" }, description = "Shows what song currently is playing", context = {
            Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void playing() {
        final AudioTrack track = ctx.getGuild().flatMap(
                guild -> ctx.getArgs().map(args -> GuildMusicManager.of(guild.getId()).getPlayer().getPlayingTrack()))
                .orElse(null);

        if (track == null) {
            ctx.reply("No music is playing!");
            return;
        }

        AudioTrackInfo info = track.getInfo();
        String uri = info.uri.startsWith("https://www.youtube.com/watch?v=")
                ? "https://youtu.be/" + info.identifier + "?t=" + (track.getPosition() / 1000)
                : info.uri;
        StringBuilder progress = new StringBuilder();
        float percent = track.getPosition() / (float) info.length;
        int totalTiles = 10;
        int tilesPlayed = (int) (totalTiles * percent);
        tilesPlayed = tilesPlayed == 0 ? 1 : tilesPlayed;
        long minutes = track.getPosition() / 1000 / 60;
        long seconds = track.getPosition() / 1000 % 60;
        progress.append("◄");
        for (int i = 0; i < totalTiles; i++) {
            if (i == tilesPlayed - 1) {
                progress.append("🔘");
            } else {
                progress.append("▬");
            }
        }
        progress.append("►\n ").append(DiscordUtil.formatTime(minutes, seconds)).append(" / ");
        minutes = track.getDuration() / 1000 / 60;
        seconds = track.getDuration() / 1000 % 60;
        progress.append(DiscordUtil.formatTime(minutes, seconds));
        try {
            YoutubeDLRequest req = new YoutubeDLRequest(uri, Nirubot.getTmpDirectory().getAbsolutePath());
            req.setOption("get-thumbnail");
            YoutubeDLResponse res = YoutubeDL.execute(req);
            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle("Now playing:")
                            .setDescription("[" + info.title + "]" + "(" + uri + ")\n" + progress.toString())
                            .setThumbnail(res.getOut()).setColor(Nirubot.getColor())).block());
        } catch (YoutubeDLException e) {
            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle("Now playing:")
                            .setDescription("[" + info.title + "]" + "(" + uri + ")\n" + progress.toString())
                            .setColor(Nirubot.getColor())).block());
            Nirubot.warning(e.getMessage());
        }
    }

    @Command(key = { "yt", "youtube",
            "yp" }, description = "Searches and plays youtube videos by given keywords", context = {
                    Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void youtube() {
        ctx.getGuild().ifPresent(guild -> ctx.getArgs().ifPresent(args -> {
            if (args.isEmpty() || !MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED,
                    MusicCondition.SAME_VOICE_CHANNEL)) {
                return;
            }

            SearchListResponse response;
            boolean loadAsNext = args.get(0).equals(NEXT_ARGUMENT);
            if (loadAsNext) {
                response = getVideos(args.stream().filter(i -> args.indexOf(i) != 0).collect(Collectors.toList()));
            } else {
                response = getVideos(args);
            }

            List<SearchResult> results = response.getItems();

            if (results == null || results.isEmpty()) {
                ctx.reply("Nothing found");
                return;
            }

            YouTubeVideo video = Nirubot.getGson().fromJson(results.get(0).toString(), YouTubeVideo.class);

            GuildMusicManager manager = GuildMusicManager.of(guild.getId());
            playYouTubeVideo(manager, video, loadAsNext);
            joinChannel(manager);
            DiscordUtil.sendEmbed(ctx,
                    spec -> spec.setColor(Nirubot.getColor()).setTitle(video.getTitle())
                            .setUrl("https://www.youtube.com/watch?v=" + video.getVideoId())
                            .setThumbnail(video.getThumbnailUrl()));
        }));
    }

    @Command(key = { "ls", "list" }, description = "Lists all queued songs", context = { Type.GUILD_CATEGORY,
            Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void list() {
        if (!MusicCondition.checkConditions(ctx, MusicCondition.MUSIC_PLAYING)) {
            return;
        }

        ctx.getGuild().ifPresent(guild -> {
            boolean isArgsEmpty = ctx.getArgs().map(List::isEmpty).orElse(false);
            if (!isArgsEmpty) {
                return;
            }

            GuildMusicManager musicManager = GuildMusicManager.of(guild.getId());

            final AudioTrack track = musicManager.getPlayer().getPlayingTrack();

            if (track == null) {
                ctx.reply("No music is playing!");
                return;
            }

            List<AudioTrackInfo> tracks = musicManager.getScheduler().getAllTrackInfos();

            StringBuilder out = new StringBuilder();

            out.append("Current: [").append(track.getInfo().title).append("](").append(track.getInfo().uri)
                    .append(")\n");

            int it = 1;

            int totalEmbs = 0;

            for (AudioTrackInfo i : tracks) {

                if (totalEmbs == 2)
                    break;

                out.append(it).append(": [").append(i.title).append("](").append(i.uri).append(")\n");

                if (out.length() > 1800) {
                    final String description = out.substring(0, out.length());
                    ctx.getChannel()
                            .ifPresent(ch -> ch
                                    .createEmbed(spec -> spec.setColor(Nirubot.getColor()).setDescription(description))
                                    .block());
                    out = new StringBuilder();

                    totalEmbs++;

                }

                it++;
            }

            if (out.length() != 0) {
                final String description = out.substring(0, out.length());
                ctx.getChannel().ifPresent(ch -> ch
                        .createEmbed(spec -> spec.setColor(Nirubot.getColor()).setDescription(description)).block());
            }

        });
    }

    @Command(key = { "remove",
            "rm" }, description = "This command removes a song from the current playing queue", context = {
                    Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT })
    public void remove() {
        ctx.getArgs().ifPresent(args -> ctx.getGuild().ifPresent(guild -> {
            GuildMusicManager manager = GuildMusicManager.of(guild.getId());
            Optional<AudioTrack> removedTrack = Optional.empty();
            if (args.size() == 1 && isNumeric(args.get(0))) {
                int index;
                try {
                    index = Integer.parseInt(args.get(0));
                } catch (NumberFormatException e) {
                    return;
                }
                removedTrack = manager.getScheduler().remove(index);
            } else if (!args.isEmpty()) {
                String songName = String.join(" ", args);
                removedTrack = manager.getScheduler().remove(songName);
            }

            removedTrack.ifPresentOrElse(
                    t -> ctx.getChannel()
                            .ifPresent(ch -> ch.createEmbed(specs -> specs
                                    .setDescription(
                                            String.format("Removed Song [%s](%s)", t.getInfo().title, t.getInfo().uri))
                                    .setColor(Nirubot.getColor())).block()),
                    () -> ctx.reply("No song to remove found!"));

        }));
    }

    public SearchListResponse getVideos(List<String> args) {
        YouTube yt = Nirubot.getYouTube();
        YouTube.Search.List search;
        try {
            search = yt.search().list(Arrays.asList("id", "snippet"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }

        search.setKey(Nirubot.getConfig().getYouTubeKey());

        StringBuilder build = new StringBuilder();

        for (String arg : args) {
            build.append(arg).append(" ");
        }

        String searchQuerry = build.substring(0, build.length() - 1);

        search.setQ(searchQuerry);

        search.setType(Collections.singletonList("video"));

        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");

        search.setMaxResults(1L);

        try {
            return search.execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String getVolumeBar(int volume) {
        int volBars = volume / 10;
        StringBuilder out = new StringBuilder();
        out.append("Volume:\n►");
        for (int i = 0; i < 10; i++) {
            if (i < volBars) {
                out.append("█");
            } else {
                out.append("░");
            }
        }
        return out.append("◄\n").append(volume).append("%").toString();
    }

    private boolean isNumeric(String str) {
        return str.chars().allMatch(i -> (i >= '0' && i <= '9'));
    }

    private void joinChannel(GuildMusicManager manager) {
        ctx.getAuthorVoiceState().flatMap(state -> state.getChannel().blockOptional())
                .ifPresent(ch -> ch.join(spec -> spec.setProvider(manager.getProvider())).block());
        ctx.getGuild().ifPresent(guild -> manager.setVolume(GuildManager.of(guild.getId()).volume()));
    }

    private void disconnectChannel() {
        ctx.getSelfVoiceState().flatMap(state -> state.getChannel().blockOptional())
                .ifPresent(ch -> ch.sendDisconnectVoiceState().block());
    }

    private void playYouTubeVideo(GuildMusicManager manager, YouTubeVideo video, boolean loadAsNext) {
        ctx.getGuild().ifPresent(guild -> {
            if (loadAsNext) {
                manager.loadAndPlayNext(video.getVideoId(), null);
            } else {
                manager.loadAndPlay(video.getVideoId(), null);
            }
        });
    }

}
