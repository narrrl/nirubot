package nirusu.nirubot.command;

import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class PrivateCommandContext implements ICommandContext {
    private static final int FILE_SIZE_MAX = 8388119;

    private final PrivateMessageReceivedEvent event;
    private final List<String> args;

    public PrivateCommandContext(PrivateMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    public PrivateMessageReceivedEvent getEvent() {
        return event;
    }

    @Override
    public List<String> getArgs() {
        return args;
    }

    public PrivateChannel getChannel() {
        return event.getChannel();
    }

    public User getAuthor() {
        return this.event.getAuthor();
    }

    public SelfUser getSelfUser() {
        return this.event.getJDA().getSelfUser();
    }

    @Override
    public long getMaxFileSize() {
        return FILE_SIZE_MAX;
    }

    @Override
    public Message getMessage() {
        return event.getMessage();
    }
}
