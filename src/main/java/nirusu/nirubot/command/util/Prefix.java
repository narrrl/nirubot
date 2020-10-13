package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.command.IPrivateCommand;
import nirusu.nirubot.command.PrivateCommandContext;
import nirusu.nirubot.core.GuildManager;

public class Prefix implements IPrivateCommand {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.getArgs();

        if (args.size() != 2) {
            return;
        }

        if (ctx.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            GuildManager.getManager(ctx.getGuild().getIdLong()).setPrefix(args.get(1));
            ctx.reply("changed prefix to " + args.get(1) + "!");
        } else {
            ctx.reply("No permissions!");
        }

    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Sets a new prefix for this guild", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("pref");
    }

    @Override
    public void execute(PrivateCommandContext ctx) {
        if (!Nirubot.isOwner(ctx.getAuthor().getIdLong())) {
            return;
        }

        if (ctx.getArgs().size() != 2) {
            return;
        }

        Nirubot.getConfig().setPrefix(ctx.getArgs().get(1));

        ctx.reply("Change prefix to " + ctx.getArgs().get(1));
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp("Sets a new prefix for this bot (global)", "", this);
    }

    
}
