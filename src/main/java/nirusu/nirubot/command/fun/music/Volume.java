package nirusu.nirubot.command.fun.music;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.GuildMusicManager;
import nirusu.nirubot.core.PlayerManager;

public final class Volume implements ICommand {

    @Override
    public void execute(final CommandContext ctx) {

        if (ctx.getArgs().size() != 2) {
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());

        int volume;

        try {
            volume = Integer.parseInt(ctx.getArgs().get(0));
        } catch (NumberFormatException e) {
            return;
        }

        GuildManager.getManager(ctx.getGuild().getIdLong()).setVolume(volume);
        musicManager.getPlayer().setVolume(volume);
    }

    @Override
    public String getKey() {
        return "volume";
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Sets the volume for this guild", gm.prefix(), getKey());
    }
}
