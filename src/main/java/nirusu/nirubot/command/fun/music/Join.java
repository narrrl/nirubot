package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.DiscordUtil;
import nirusu.nirubot.core.GuildManager;

public class Join implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        VoiceChannel channel = DiscordUtil.findVoiceChannel(ctx.getMember());

        ctx.getGuild().getAudioManager().openAudioConnection(channel);
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Bot joins your channel", gm.prefix(), getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("j");
    }
    
}
