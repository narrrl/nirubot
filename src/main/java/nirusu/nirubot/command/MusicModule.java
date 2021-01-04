package nirusu.nirubot.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.rest.util.Color;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.audio.GuildMusicManager;
import nirusu.nirubot.core.audio.PlayerManager;
import nirusu.nirubot.util.MusicCondition;
import nirusu.nirubot.util.YouTubeVideo;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;


public class MusicModule extends BaseModule {
    @Command( key = { "p", "play", "pl"}, description = "Plays a song", 
        context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void play() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED)) {
            return;
        }

        String link = args.get(0);
        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        ctx.getAuthorVoiceState().ifPresent(state -> {
            VoiceChannel ch = state.getChannel().block();
            PlayerManager.getInstance().loadAndPlay(ctx, link);
            ch.join(con -> con.setProvider(musicManager.getProvider())).block();
        });

    }

    @Command( key = { "skip", "next", "s", "sk"}, description = "Skips the current song", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void skip() {
        Guild guild = ctx.getGuild().orElseThrow();

        if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL, MusicCondition.MUSIC_PLAYING)) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);

        AudioTrack prev = musicManager.getPlayer().getPlayingTrack();

        PlayerManager.getInstance().next(guild);

        AudioTrack next = musicManager.getPlayer().getPlayingTrack();
        String prevText = "[" + prev.getInfo().title + "](" + prev.getInfo().uri + ")";
        String nextText = next != null ? "[" + next.getInfo().title + "](" + next.getInfo().uri + ")" : "End of queue!";
        ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
            spec.setColor(Color.of(Nirubot.getColor().getRGB()))
            .setTitle("Skipped Song!")
            .addField("Skipped:", prevText, true)
            .addField("Next:", nextText, true)).block()
        );
    }

    @Command(key = {"vol", "vl", "volume"}, description = "Sets the volume for the bot")
    public void volume() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (args.size() != 1) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        int volume;
        try {
            volume = Integer.parseInt(args.get(0));
        } catch ( NumberFormatException e) {
            ctx.reply(String.format("%s is not a valid volume", args.get(0)));
            return;
        }

        if (volume < 0) return;

        volume = volume > 100 ? 100 : volume;

        GuildManager.getManager(guild.getId().asLong()).setVolume(volume);
        musicManager.setVolume(volume);

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
        out.append("◄\n" + GuildManager.getManager(ctx.getGuild().orElseThrow().getId().asLong()).volume() + "%");

        ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
            spec.setColor(Color.of(Nirubot.getColor().getRGB()))
            .setDescription(out.toString())).block()
        );
    }

    @Command(key = {"join", "j"}, description = "Joins into the channel of the author", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void join() {
        Guild guild = ctx.getGuild().orElseThrow();
        int argsSize = ctx.getArgs().map(args -> args.size()).orElse(-1);

        if (argsSize != 0) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED)) {
            return;
        }
        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        ctx.getAuthorVoiceState().ifPresent(state 
            -> state.getChannel().blockOptional().ifPresent(ch 
            -> ch.join(con -> con.setProvider(musicManager.getProvider()))
                .block()
        ));

    }

    @Command(key = {"leave", "left", "l"}, description = "Leaves the current voice channel", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void leave() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!args.isEmpty()) {
            return;
        }
        
        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED, MusicCondition.BOT_CONNECTED, MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }
        
        ctx.getSelfVoiceState().ifPresent(state 
            -> state.getChannel().blockOptional().ifPresent(ch 
            -> ch.sendDisconnectVoiceState()
            .block()));
        PlayerManager.getInstance().destroy(guild.getId().asLong());
    }

    @Command(key = {"repeat","loop","rp"}, description = "Repeats the current playlist", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void repeat() {
        List<String> args = ctx.getArgs().orElseThrow();

        if (!args.isEmpty()) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.BOT_CONNECTED, MusicCondition.SAME_VOICE_CHANNEL)) {
            return;
        }

        if (PlayerManager.getInstance().repeat(ctx.getGuild().orElseThrow())) {
            ctx.reply("Now repeating current playlist!");
        } else {
            ctx.reply("Stopped repeating playlist!");
        }
        
    }

    @Command(key = {"pause", "resume"}, description = "Pause/Resume music", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void pause() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!args.isEmpty()) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.SAME_VOICE_CHANNEL, MusicCondition.MUSIC_PLAYING)) {
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(guild);

        manager.pause(guild, !musicManager.getPlayer().isPaused());
    }

    @Command(key = {"playing", "nowplaying", "np"}, description = "Shows what song currently is playing", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void playing() {
        Guild guild = ctx.getGuild().orElseThrow();
        List<String> args = ctx.getArgs().orElseThrow();

        if (!args.isEmpty()) {
            return;
        }

         PlayerManager manager = PlayerManager.getInstance();
        final AudioTrack track = manager.getPlaying(guild);

        if (track == null) {
            ctx.reply("No music is playing!");
            return;
        }

        AudioTrackInfo info = track.getInfo();
        String uri = info.uri.startsWith("https://www.youtube.com/watch?v=") 
            ? "https://youtu.be/" + info.identifier +  "?t=" + (track.getPosition() / 1000) : info.uri;
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
        progress.append("►\n " + DiscordUtil.formatTime(minutes, seconds)+ " / ");
        minutes = track.getDuration() / 1000 / 60;
        seconds = track.getDuration() / 1000 % 60;
        progress.append(DiscordUtil.formatTime(minutes, seconds));
        try {
            YoutubeDLRequest req = new YoutubeDLRequest(uri, Nirubot.getTmpDirectory().getAbsolutePath());
            req.setOption("get-thumbnail");
            YoutubeDLResponse res = YoutubeDL.execute(req);
            ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
                spec.setTitle("Now playing:")
                    .setDescription("[" + info.title + "]" + "(" + uri + ")\n" + progress.toString())
                    .setThumbnail(res.getOut())
                    .setColor(Color.of(Nirubot.getColor().getRGB()))).block()
            );
        } catch (YoutubeDLException e) {
            ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
                spec.setTitle("Now playing:")
                    .setDescription("[" + info.title + "]" + "(" + uri + ")\n" + progress.toString())
                    .setColor(Color.of(Nirubot.getColor().getRGB()))).block()
            );
            Nirubot.warning(e.getMessage());
        }
    }

    @Command(key = {"yt", "youtube", "yp"}, 
        description = "Searches and plays youtube videos by given keywords", 
        context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void youtube() {

        List<String> args = ctx.getArgs().orElseThrow();
        Guild guild = ctx.getGuild().orElseThrow();

        if (args.isEmpty()) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.USER_CONNECTED)) {
            return;
        }

        Optional<VoiceChannel> channel = ctx.getAuthorVoiceState().orElseThrow().getChannel().blockOptional();

        SearchListResponse response = getVideos(args);

        List<SearchResult> results = response.getItems();

        if (results == null) {
            ctx.reply("No videos found!");
            return;
        }
        
        if (results.isEmpty()) {
            ctx.reply("Nothing found");
            return;
        }

        YouTubeVideo video = Nirubot.getGson().fromJson(results.get(0).toString(), YouTubeVideo.class);
        PlayerManager.getInstance().loadAndPlay(ctx, video.getVideoId());

        channel.ifPresent(ch -> ch.join(spec -> spec.setProvider(PlayerManager.getInstance().getGuildMusicManager(guild).getProvider())).block());

        ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec -> 
            spec.setColor(Color.of(Nirubot.getColor().getRGB()))
                .setTitle(video.getTitle())
                .setUrl("https://www.youtube.com/watch?v=" + video.getVideoId())
                .setThumbnail(video.getThumbnailUrl())).block()
        );
    }


    @Command(key = {"ls", "list"}, description = "Lists all queued songs", context = {Type.GUILD_CATEGORY, Type.GUILD_NEWS, Type.GUILD_TEXT})
    public void list() {

        Guild guild = ctx.getGuild().orElseThrow();

        if (!ctx.getArgs().orElseThrow().isEmpty()) {
            return;
        }

        if (!MusicCondition.checkConditions(ctx, MusicCondition.MUSIC_PLAYING)) {
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(guild);

        final AudioTrack track = manager.getPlaying(guild);

        if (track == null) {
            ctx.reply("No music is playing!");
            return;
        }

        ArrayList<AudioTrackInfo> tracks = musicManager.getScheduler().getAllTrackInfos();

        StringBuilder out = new StringBuilder();

        out.append("Current: [" + track.getInfo().title + "](" + track.getInfo().uri + ")\n");

        int it = 1;

        int totalEmbs = 0;

        for (AudioTrackInfo i : tracks) {

            if (totalEmbs == 2)
                break;

            out.append(it + ": [" + i.title + "](" + i.uri + ")\n");

            if (out.length() > 1800) {
                final String description = out.toString().substring(0, out.length());
                ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
                    spec.setColor(Color.of(Nirubot.getColor().getRGB()))
                        .setDescription(description)).block()
                );
                out = new StringBuilder();

                totalEmbs++;

            }

            it++;
        }

        if (out.length() != 0) {
            final String description = out.toString().substring(0, out.length());
            ctx.getChannel().ifPresent(ch -> ch.createEmbed(spec ->
                spec.setColor(Color.of(Nirubot.getColor().getRGB()))
                    .setDescription(description)).block()
            );
        }
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

        for (int i = 0; i < args.size(); i++) {
            build.append(args.get(i)).append(" ");
        }

        String searchQuerry = build.substring(0, build.length() - 1);

        search.setQ(searchQuerry);

        search.setType(Arrays.asList("video"));

        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");

        search.setMaxResults(1L);

        try {
            return search.execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    
}
