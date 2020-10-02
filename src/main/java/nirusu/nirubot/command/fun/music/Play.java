package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
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

        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        try {
            PlayerManager.getInstance().loadAndPlay(ctx, ctx.getArgs().get(1));
        } catch (IllegalArgumentException e) {
            return;
        }

        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Nirubot.getColor()).setTitle("Song/Playlist loaded!");
        ctx.reply(emb.build());
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Connects the bot to your channel and starts playing the song", gm.prefix(),
                this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("p", "pl");
    }
}
