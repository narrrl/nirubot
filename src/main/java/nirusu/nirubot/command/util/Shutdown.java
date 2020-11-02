package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.ICommandContext;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.PrivateCommandContext;
import nirusu.nirubot.core.GuildManager;

public class Shutdown implements IPrivateCommand {

    @Override
    public void execute(CommandContext ctx) {
        start(ctx);
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("This command will shutdown the bot", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Collections.singletonList("shutd");
    }

    @Override
    public void execute(PrivateCommandContext ctx) {
        start(ctx);
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("This command will shutdown the bot", "", this);
    }


    public void start(ICommandContext ctx) {

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
    
}
