package nirusu.nirubot.command.fun.music;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;
import nirusu.nirubot.util.YouTubeVideo;

public class YTSearch implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() < 2) {
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        SearchListResponse response = getVideos(ctx.getArgs());

        List<SearchResult> results = response.getItems();

        if (results == null) {
            ctx.reply("No videos found!");
        }


        YouTubeVideo video = Nirubot.getGson().fromJson(results.get(0).toString().replace("\"default\"", "\"default_\""), YouTubeVideo.class);
        EmbedBuilder emb = new EmbedBuilder();

        emb.setColor(Nirubot.getColor()).setTitle(video.getTitle(), "https://www.youtube.com/watch?v=" + video.getVideoId()).setThumbnail(video.getThumbnailUrl());
        try {
            PlayerManager.getInstance().loadAndPlay(ctx, video.getVideoId());
        } catch (IllegalArgumentException e) {
            return;
        }
        ctx.reply(emb.build());

    }


    public SearchListResponse getVideos(List<String> args) {
        YouTube yt = Nirubot.getYouTube();
        YouTube.Search.List search;
        try {
             search = yt.search().list(Arrays.asList(new String[] { "id", "snippet" }));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }

        search.setKey(Nirubot.getConfig().getYouTubeKey());

        StringBuilder build = new StringBuilder();

        for (int i = 1; i < args.size(); i++) {
            build.append(args.get(i)).append(" ");
        }

        String searchQuerry = build.substring(0, build.length() - 1);

        search.setQ(searchQuerry);

        search.setType(Arrays.asList(new String[]{ "video" }));

        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");

        search.setMaxResults(1L);
        
        try {
            return search.execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Searches for a song on youtube and queues it", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("yt", "yp");
    }
    
}
