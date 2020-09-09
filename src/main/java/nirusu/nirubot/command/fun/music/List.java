package nirusu.nirubot.command.fun.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

import java.util.ArrayList;

public final class List implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());
        final AudioTrack track = manager.getPlaying(musicManager);

        if (track == null) {
            ctx.reply("No music is playing!");
            return;
        }

        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Nirubot.getColor()).setThumbnail(ctx.getGuild().getIconUrl()).setTitle("Current Queue:");

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

                emb.setDescription(out.toString().substring(0, out.length()));
                ctx.reply(emb.build());
                out = new StringBuilder();

                totalEmbs++;

            }

            it++;
        }

        if (out.length() != 0) {
            emb.setDescription(out.toString().substring(0, out.length()));
            ctx.reply(emb.build());

        }

    }


    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Lists all queued songs", gm.prefix(), getKey());
    }
}
