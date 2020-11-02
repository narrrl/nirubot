package nirusu.nirubot.command;

import java.io.File;
import java.util.List;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public interface ICommandContext {

    public default void reply(final String message) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(message).queue());
    }

    public default void reply(final MessageEmbed emb) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(emb).queue());
    }

    public default List<Message.Attachment> getAttachments() {
        return getMessage().getAttachments();
    };

    public default String getMessageRaw() {
        List<String> args = getArgs();

        if (args.size() < 2) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.size(); i++) {
            builder.append(args.get(i)).append(" ");
        }

        return builder.substring(0, builder.length() - 1);
    }
	public default void sendFile(File f, String name) {
        getChannel().sendFile(f, name).complete();
    }

    public MessageChannel getChannel();

    public IMentionable getAuthor();
    
    public Message getMessage();

	public long getMaxFileSize();

    public List<String> getArgs();
    

    
}
