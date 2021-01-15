package nirusu.nirubot.command;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.help.HelpCreator;
import nirusu.nirubot.core.help.CommandMeta.Metadata;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class UtilityModule extends BaseModule {

    @Command(key = "ping", description = "This command pings the bot")
    public void ping() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty())
                return;

            long curr = System.currentTimeMillis();
            ctx.reply("Pong!").ifPresent(mes -> {
                long ping = mes.getTimestamp().toEpochMilli() - curr;
                // not very accurate ¯\_(ツ)_/¯
                mes.edit(edit -> edit.setContent(String.format("Pong: %d ms", ping))).block();
            });
        });
    }

    @Command(key = { "help", "h" }, description = "Help command")
    public void help() {
        ctx.getArgs().ifPresent(args -> {
            HelpCreator h = Nirubot.getNirubot().getHelpCreator();
            String help;
            String title;
            String footNote;
            String prefix = ctx.getGuild().map(guild -> GuildManager.of(guild.getId()).prefix()).orElse("");
            switch (args.size()) {
                case 0 -> {
                    help = h.listModules();
                    title = "All Command-Modules";
                    footNote = String.format("List all commands of a module with `%shelp <module>`", prefix);
                }
                case 1 -> {
                    help = h.getModuleWithName(args.get(0)).map(h::listCommandsFor)
                            .orElse(String.format("No module %s found", args.get(0)));
                    title = String.format("All Commands of Module %s", args.get(0));
                    footNote = String.format("Get info about a command with `%shelp %s <command>`", prefix,
                            args.get(0));
                }
                case 2 -> {
                    Metadata data = h.getModuleWithName(args.get(0))
                            .map(module -> h.metadataForCommand(module, args.get(1)))
                            .orElse(new Metadata().setName("")
                                    .setDescription(String.format("No module %s found", args.get(0))).setSyntax(""));
                    title = String.format("Help for Command %s", args.get(1));
                    help = data.getDescription().concat("\nAliases: ").concat(data.getAliases());
                    footNote = data.getSyntax().equals("") ? ""
                            : "Syntax: `".concat(data.getSyntax().replace("<prefix>", prefix)).concat("`");
                }
                default -> {
                    return;
                }
            }

            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle(title)
                            .setDescription(footNote.equals("") ? help : help.concat("\n\n".concat(footNote)))
                            .setColor(Nirubot.getColor())).block());
        });
    }
}
