package nirusu.nirubot.command.music;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Play implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        // TODO: implement

    }

    @Override
    public String getKey() {
        return "play";
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return null;
    }
    
}
