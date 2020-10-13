package nirusu.nirubot.command;

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

    public MessageChannel getChannel();
    
}
