package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;

public final class Leave implements ICommand {
    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.destroy(ctx.getGuild());
        ctx.getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Stops the current music and deletes queue", gm.prefix(), getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("lv");
    }
}
