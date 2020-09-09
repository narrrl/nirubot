package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Shutdown implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        if (!Nirubot.isOwner(ctx.getAuthor().getIdLong())) {
            ctx.reply("Not enough permissions");
            return;
        }

        ctx.getChannel().sendMessage("Bot is shutting down!").complete();
        Nirubot.getNirubot().shutdown();

    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This command will shutdown the bot", gm.prefix(), getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("shutd");
    }
    
}
