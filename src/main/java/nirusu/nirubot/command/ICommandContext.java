package nirusu.nirubot.command;

import java.io.File;
import java.util.List;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;

public interface ICommandContext {

    public default void reply(final String message) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(message).queue());
    }

    public default void reply(final MessageEmbed emb) {
        getChannel().sendTyping().queue(rep ->
            getChannel().sendMessage(emb).queue());
    }   

	public default void sendFile(File f, String name) {
        getChannel().sendFile(f, name).complete();
    }

    public MessageChannel getChannel();

	public IMentionable getAuthor();

	public long getMaxFileSize();

	public List<String> getArgs();
    
}
