package nirusu.nirubot.command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface IPrivateCommand extends ICommand {

    public void execute(final PrivateCommandContext ctx);
    public MessageEmbed helpMessage();
}
