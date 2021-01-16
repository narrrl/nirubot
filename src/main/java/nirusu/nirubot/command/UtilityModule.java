package nirusu.nirubot.command;

import discord4j.core.object.entity.User;
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
                    // just all commands
                    help = h.listModules();
                    title = "All Command-Modules";
                    footNote = String.format("List all commands of a module with `%shelp <module>`", prefix);
                }
                case 1 -> {
                    // get all commands of that module
                    help = h.getModuleWithName(args.get(0)).map(h::listCommandsFor)
                            .orElse(String.format("No module %s found", args.get(0)));
                    // set title
                    title = String.format("All Commands of Module %s", args.get(0));
                    // help to get more info about a command in that module
                    footNote = String.format("Get info about a command with `%shelp %s <command>`", prefix,
                            args.get(0));
                }
                case 2 -> {
                    // get meta data for command
                    Metadata data = h.getModuleWithName(args.get(0))
                            .map(module -> h.metadataForCommand(module, args.get(1))).orElse(new Metadata().setName("")
                                    .setDescription(String.format("No module %s found", args.get(0))).setSyntax(""));
                    // set title for embed
                    title = String.format("Help for Command %s", args.get(1));
                    // help is description and a list of aliases for that command
                    help = data.getDescription().equals("Command not found") ? data.getDescription()
                            : data.getDescription().concat("\nAliases: ").concat(data.getAliases());
                    // foot note is the syntax for that command
                    footNote = data.getSyntax().equals("") ? ""
                            : "Syntax: `".concat(data.getSyntax().replace("<prefix>", prefix)).concat("`");
                }
                // if args size is not right return and do nothing
                default -> {
                    return;
                }
            }

            // create embed and send it
            ctx.getChannel()
                    .ifPresent(ch -> ch.createEmbed(spec -> spec.setTitle(title)
                            .setDescription(footNote.equals("") ? help : help.concat("\n\n".concat(footNote)))
                            .setColor(Nirubot.getColor())).block());
        });
    }

    @Command(key = "avatar", description = "Gets the avatar of the tagged user")
    public void avatar() {
        ctx.getArgs().ifPresent(args -> {
            if (args.size() != 1) {
                return;
            }

            User u = ctx.getEvent().getMessage().getUserMentions().blockFirst();
            if (u != null) {

                // get avatar url and add the size tag to get a bigger size
                ctx.getChannel().ifPresent(
                        ch -> ch.createEmbed(specs -> specs.setImage(u.getAvatarUrl().concat("?size=2048"))).block());
            }
        });
    }
}
