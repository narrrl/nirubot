package nirusu.nirubot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;

public interface ICommand {

    public void execute(CommandContext ctx);

    public String getKey();

    public MessageEmbed helpMessage(GuildManager gm);

    public static MessageEmbed createHelp(final String message, final String prefix, final String key) {
        return new EmbedBuilder().setColor(Nirubot.getColor()).setDescription(message)
            .setTitle("Help for " + prefix + key).build();
    }
    
}
