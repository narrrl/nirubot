package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public final class Next implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        if (DiscordUtil.findVoiceChannel(ctx.getSelfMember()) == null) {
            ctx.reply("No music is playing");
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }


        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());
        AudioTrack prev = musicManager.getPlayer().getPlayingTrack();

        if (prev == null) {
            ctx.reply("Nothing is playing!");
            return;
        }

        manager.next(musicManager);
        AudioTrack next = musicManager.getPlayer().getPlayingTrack();
        String prevText = "[" + prev.getInfo().title + "](" + prev.getInfo().uri + ")";
        String nextText = next != null ? "[" + next.getInfo().title + "](" + next.getInfo().uri + ")" : "End of queue";
        ctx.reply(new EmbedBuilder().setTitle("Skipped Song!")
        .setDescription("Skipped: \n" + prevText + "\n" + "Now playing: \n" + nextText).setColor(Nirubot.getColor()).build());
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("skips the current song", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("skip", "s");
    }

}
