package nirusu.nirubot.command.fun.music;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public final class Pause implements ICommand {

    @Override
    public void execute(final CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }
        final PlayerManager manager = PlayerManager.getInstance();
        final GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());
        manager.pause(musicManager, !musicManager.getPlayer().isPaused());
    }

    @Override
    public String getKey() {
        return "pause";
    }

    @Override
    public MessageEmbed helpMessage(final GuildManager gm) {
        return ICommand.createHelp("Pauses the current queue", gm.prefix(), getKey());
    }

}
