package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

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

public final class Playing implements ICommand {

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
        AudioTrackInfo info = track.getInfo();
        emb.setColor(Nirubot.getColor()).setThumbnail(ctx.getGuild().getIconUrl()).setTitle("Now playing:")
                .setDescription("[" + info.title + "]" + "(" + info.uri + ")");
        ctx.reply(emb.build());

    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Shows the current playing song", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("np");
    }
}
