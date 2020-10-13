package nirusu.nirubot.command.util;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;

public class Help implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        Iterable<ICommand> commands = ServiceLoader.load(ICommand.class);

        List<String> args = ctx.getArgs();

    
        if (args.size() == 1) {
            EmbedBuilder emb = new EmbedBuilder();
            StringBuilder commandNames = new StringBuilder();
            commands.forEach(cmd -> commandNames.append(cmd.getKey()).append(", "));
            emb.setTitle("All commands:").setColor(Nirubot.getColor()).setDescription(commandNames.substring(0, commandNames.length() - 2));
            ctx.reply(emb.build());
        } else if (args.size() == 2) {
            ICommand cmd = StreamSupport.stream(commands.spliterator(), false)
            .filter(f -> 
                (f.getKey().equals(args.get(1)) || f.alias().stream().anyMatch(p -> p.equals(args.get(1))))
                ).findFirst().orElse(new Help());
            ctx.reply(cmd.helpMessage(GuildManager.getManager(ctx.getGuild().getIdLong())));
        }
    }



    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return new EmbedBuilder()
        .setDescription("Use " + gm.prefix() + getKey() + " to list all commands and " + gm.prefix() + getKey() + " <command> for more info about a command")
        .setColor(Nirubot.getColor()).build();
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("h");
    }
    
}
