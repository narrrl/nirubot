package nirusu.nirubot.command.fun.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public class Remove implements ICommand {

    @Override
    public void execute(final CommandContext ctx) {

        final List<String> args = ctx.getArgs();

        if (args.size() < 2) {
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
        }

        PlayerManager mg = PlayerManager.getInstance();
        GuildMusicManager musicManager = mg.getGuildMusicManager(ctx.getGuild());

       AudioTrack tr = null; 
        try {
            tr = mg.remove(musicManager, Integer.parseInt(args.get(1)));
        } catch (NumberFormatException e) {
            StringBuilder b = new StringBuilder();
            for (int i = 1; i < args.size(); i++) {
                b.append(args.get(i)).append(" ");
            }
            tr = mg.remove(musicManager, b.substring(0, b.length() - 1));
        }

        if (tr != null) {
            ctx.reply("Song " + tr.getInfo().title + " removed!");
        } else {
            ctx.reply("Song not found!");
        }

    }

    @Override
    public MessageEmbed helpMessage(final GuildManager gm) {
        return ICommand.createHelp("Removes song at an given position", gm.prefix(), getKey());
    }
    
}
