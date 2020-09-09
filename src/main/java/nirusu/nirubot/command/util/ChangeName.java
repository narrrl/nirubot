package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class ChangeName implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        if (Nirubot.isOwner(ctx.getAuthor().getIdLong())) {
            List<String> args = ctx.getArgs();

            if (args.size() < 2) {
                return;
            }

            StringBuilder builder = new StringBuilder();

            for (int i = 1; i < args.size(); i++) {
                builder.append(args.get(i)).append(" ");
            }

            String newName = builder.substring(0, builder.length() - 1);

            if (newName.length() > 32 || newName.length() < 2) {
                ctx.reply("Nicknames can't be less than 2 and more than 32");
                return;
            }

            ctx.getJDA().getSelfUser().getManager().setName(newName).complete();
            ctx.reply("Updated name");

        } else {
            ctx.reply("Not enough permissions");
        }
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Change the name of the bot user", gm.prefix(), getKey());
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("cname", "chna");
    }
    
}
