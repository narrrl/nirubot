package nirusu.nirubot.command.fun.music;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import nirusu.nirubot.command.CommandContext;
import nirusu.nirubot.command.ICommand;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.PlayerManager;

public class Playlist implements ICommand {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        if (args.size() != 3) {
            return;
        }

        GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong());

        if (args.get(1).equals("load")) {
            String[] songs;
            try {
                songs = gm.getPlaylist(args.get(2));
            } catch (IllegalArgumentException e) {
                ctx.reply(e.getMessage());
                return;
            }
            for (String uri : songs) {
                try {
                    PlayerManager.getInstance().loadAndPlay(ctx, uri);
                } catch (IllegalArgumentException e) {
                    // nice
                }
            }
            ctx.reply("Playlist loaded!");
        } else if (args.get(1).equals("save")) {
            String[] songs = PlayerManager.getInstance().getCurrentSongs(ctx.getGuild());
            gm.addPlaylist(args.get(2), songs);
            ctx.reply("Playlist " + args.get(2) + " saved!");
        } else if (args.get(1).equals("remove")) {
            gm.removePlaylist(args.get(2));
            ctx.reply("Removed playlist " + args.get(2) + " if it was present");
        }
    }

    @Override
    public List<String> alias() {
        return Arrays.asList("playl", "pyl");
    }

    @Override
    public MessageEmbed helpMessage(GuildManager gm) {
        return ICommand.createHelp(gm.prefix()
        + "playlist save <name> saves current playing queue under a given name for the guild which can then be loaded anytime in the future with "
        + gm.prefix() + "playlist load <name>," + gm.prefix() + "\n An old playlist can be deleted with " + gm.prefix() + "playlist remove <name>", gm.prefix(), this);
    }

}
