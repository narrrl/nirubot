package nirusu.nirubot.command;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;

public interface ICommand {

    public void execute(CommandContext ctx);

    public default String getKey() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public List<String> alias();

    public MessageEmbed helpMessage(GuildManager gm);

    public static MessageEmbed createHelp(final String message, final String prefix, final ICommand cmd) {
        StringBuilder aliases = new StringBuilder();
        cmd.alias().forEach(str -> aliases.append(str).append(", "));
        return new EmbedBuilder().setColor(Nirubot.getColor())
            .setDescription(message + "\n\nAlias for command:\n" + aliases.substring(0, aliases.length() - 2))
            .setTitle("Help for **" + prefix + cmd.getKey() + "**").build();
    }
    
}