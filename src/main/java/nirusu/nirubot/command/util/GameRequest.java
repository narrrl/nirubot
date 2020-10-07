package nirusu.nirubot.command.util;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.listener.GameRequestListener;
import nirusu.nirubot.util.GameRequestManager;

public class GameRequest implements ICommand {

    @Override
    public void execute(CommandContext ctx) {

        List<String> args = ctx.getArgs();

        if (args.size() < 4) {
            return;
        }

        List<User> users = ctx.getMessage().getMentionedUsers();

        if (users.size() < 1) {
            return;
        }
        
        GameRequestManager rq;

        try {
             rq = new GameRequestManager(users, args.get(2), args.get(1), ctx.getChannel(), ctx.getAuthor());
        } catch (IllegalArgumentException e) {
            ctx.reply(e.getMessage());
            return;
        }

        try {
            GameRequestListener.getInstance().addManager(rq);
        } catch (IllegalArgumentException e) {
            ctx.reply(e.getMessage());
            return;
        }

        rq.getChannel().sendMessage(rq.toEmb()).queue();
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("gq", "game", "gr", "request");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp(gm.prefix() + "GameRequest <game> <date> <user1> <user2>...", gm.prefix(), this);
    }
    
}
