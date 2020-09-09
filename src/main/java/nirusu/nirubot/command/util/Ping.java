package nirusu.nirubot.command.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Ping implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        if (ctx.getArgs().size() != 1) {
            return;
        }

        ctx.getChannel().sendMessage("Pong!")
                .queue(edit -> edit.editMessageFormat("Pong: %d ms", ctx.getJDA().getGatewayPing()).queue());
    }



    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("Ping command to check if the bot is online and working. Also sends the ping of the bot", gm.prefix(), getKey());
    }
    
}
