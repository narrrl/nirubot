package nirusu.nirubot.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nirusu.nirubot.annotation.Command;
import nirusu.nirubot.exception.InvalidContextException;

public class CommandContext {
    private List<String> args;
    private Command.Context context;
    private final MessageReceivedEvent e;

    public CommandContext(@Nonnull MessageReceivedEvent e,@Nonnull final Command.Context context) {
        this.e = e;
        this.context = context;
        args = new ArrayList<>();
    }

    public CommandContext setArgs(@Nonnull final List<String> args) {
        this.args = args;
        return this;
    }


    public MessageReceivedEvent getEvent() {
        return this.e;
    }

    public boolean isContext(Command.Context o) {
        return this.context.equals(o);
    }

	public void reply(String message) {
        if (isContext(Command.Context.GUILD)) {

            e.getChannel().sendMessage(message).queue();

        } else if (isContext(Command.Context.PRIVATE)) {

            e.getPrivateChannel().sendMessage(message).queue();

        } else {

            throw new InvalidContextException("Context not set!");

        }
    }

    public List<String> getArgs() {
        return this.args;
    }

    public Guild getGuild() {
        if (isContext(Command.Context.GUILD)) {
            return e.getGuild();
        }
        throw new InvalidContextException("No guild in private available");
    }

}
