package nirusu.nirubot.command;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Class to make writing discord commands a bit more easy
 */
public class CommandContext implements ICommandContext {
    private final GuildMessageReceivedEvent event;
    private final List<String> args;

    public CommandContext(@Nonnull final GuildMessageReceivedEvent event, @Nonnull  final List<String> args) {
        this.event = event;
        this.args = args;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return args;
    }

    public synchronized void reply(final String message) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(message).queue());
    }

    public synchronized void reply(final MessageEmbed emb) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(emb).queue());
    }
}
