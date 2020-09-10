package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Playlist implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        // TODO: implement
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("playl", "pyl");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("'" + gm.prefix() 
        + "playlist save <name> ' saves current playing queue under a given name for the guild which can then be loaded anytime in the future with " 
        + gm.prefix() + "playlist load <name>", gm.prefix(), this);
    }
    
}
