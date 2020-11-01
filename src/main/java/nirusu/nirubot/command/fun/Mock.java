package nirusu.nirubot.command.fun;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.*;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.util.RandomHttpClient;

public class Mock implements IPrivateCommand {

    @Override
    public void execute(CommandContext ctx) {

        String message = ctx.getMessageRaw();

        message = randomize(message);

        if (!message.equals("")) ctx.reply(message);
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp("makes your message retarded", gm.prefix(), this);
    }

    @Override
    public List<String> alias() {
        return Collections.emptyList();
    }

    @Override
    public void execute(PrivateCommandContext ctx) {

        List<String> args = ctx.getArgs();

        if (args.size() == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        String message = builder.substring(0, builder.length() - 1);

        message = randomize(message);

        if (!message.equals("")) ctx.reply(message);

    }

    private String randomize(final String message) {
        List<Byte> nums;
        try {
            nums = RandomHttpClient.getRandomBit(message.length());
        } catch (IOException e) {
            return "";
        }
        Iterator<Byte> it = nums.iterator();
        StringBuilder builder = new StringBuilder();
        char[] ch = message.toCharArray();
        for (int i = 0; i < message.length() && it.hasNext(); i++) {
            byte num = it.next();
            char c = num == 0 ? Character.toUpperCase(ch[i]) : Character.toLowerCase(ch[i]);
            builder.append(c);
        }

        return builder.toString();
    }

    @Override
    public MessageEmbed helpMessage() {
        return ICommand.createHelp(" makes your message retarded", "", this);
    }
    
}
