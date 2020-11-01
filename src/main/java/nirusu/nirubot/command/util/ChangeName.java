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

            String newName = ctx.getMessageRaw();

            if (newName.length() > 32 || newName.length() < 2) {
                ctx.reply("Nicknames can't be less than 2 and more than 32");
                return;
            }

            ctx.getSelfUser().getManager().setName(newName).complete();
            ctx.reply("Updated name");

        } else {
            ctx.reply("Not enough permissions");
        }
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Change the name of the bot user", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("cname", "chna");
    }
    
}
