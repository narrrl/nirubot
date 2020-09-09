package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Prefix implements ICommand {

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
        return ICommand.createHelp("Sets a new prefix for this guild", gm.prefix(), getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("pref");
    }

    
}
