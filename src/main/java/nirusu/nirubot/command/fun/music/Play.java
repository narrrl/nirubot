package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;

public final class Play implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 2) {
            return;
        }

        VoiceChannel channel = DiscordUtil.findVoiceChannel(ctx.getSelfMember());

        if (channel != null && !DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager.getInstance().loadAndPlay(ctx, ctx.getArgs().get(1), null);
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Connects the bot to your channel and starts playing the song", gm.prefix(),
                getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("p", "pl");
    }
}
