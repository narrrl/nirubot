package nirusu.nirubot.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;

import discord4j.core.object.entity.User;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.core.help.HelpCreator;
import nirusu.nirubot.core.help.CommandMeta.Metadata;
import nirusu.nirubot.util.DiscordUtil;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class UtilityModule extends BaseModule {
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String OS = String.format("%s - %s", System.getProperty("os.name"),
            System.getProperty("os.version"));
    private static final String ARCH = System.getProperty("os.arch");
    private static final int MAX_PROCS = Runtime.getRuntime().availableProcessors();

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

            // get avatar url and add the size tag to get a bigger size
            ctx.getChannel().ifPresent(
                    ch -> ch.createEmbed(specs -> specs.setImage(u.getAvatarUrl().concat("?size=2048"))).block());
        });
    }

    @Command(key = "invite", description = "Sends a invite for the bot, to invite it to your server")
    public void invite() {
        if (!ctx.getArgs().map(List::isEmpty).orElse(false)) {
            return;
        }

        DiscordUtil.sendEmbed(ctx, spec -> spec.setTitle("Invite this bot to your server!")
                .setUrl(String.format("https://discord.com/api/oauth2/authorize?client_id=%d&permissions=8&scope=bot",
                        ctx.getSelf().map(self -> self.getId().asLong()).orElse(-1L)))
                .setImage("https://media1.tenor.com/images/b7254b1f7083b0d8088905de997ef5bb/tenor.gif"));

    }

    @Command(key = "info", description = "Get the current application informations")
    public void info() {
        ctx.getArgs().ifPresent(args -> {
            if (!args.isEmpty()) {
                return;
            }

            DiscordUtil.sendEmbed(ctx, spec -> spec.setTitle(ctx.getSelf().map(User::getUsername).orElse(""))
                    .setDescription(getApplicationInfo()).setColor(Nirubot.getColor())
                    .setThumbnail(ctx.getSelf().map(User::getAvatarUrl).orElse("")).setTimestamp(Instant.now()));

        });
    }

    private synchronized String getApplicationInfo() {
        StringBuilder owners = new StringBuilder();
        for (long l : Nirubot.getConfig().getOwners()) {
            owners.append("<@").append(l).append(">").append(", ");
        }
        String ownerString = owners.length() < 2 ? "" : owners.substring(0, owners.length() - 2);
        int load = 0;
        try {
            String[] cmdline = { "sh", "-c", "echo $(vmstat 1 2|tail -1|awk '{print $15}')" };
            Process pr = Runtime.getRuntime().exec(cmdline);
            StringBuilder output = new StringBuilder();
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                // Read the output from the command
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    output.append(s);
                }
            }
            load = 100 - Integer.parseInt(output.toString());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        String loadString = getUsageBar(load);
        return String.format(
                "**Java Version:** %s%n**OS:** %s%n**Bot-Owner:** %s%n**Used Memory:** %dMb%n**Cores:** %d%n**Architecture:** %s%n**CPU-Usage:** %s",
                JAVA_VERSION, OS,  ownerString, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000),
                MAX_PROCS, ARCH, load + "%\n" + loadString);
    }

    private String getUsageBar(int usage) {
        StringBuilder usageBar = new StringBuilder();
        for (int i = 10; i <= 100; i += 10) {
            if (i <= usage) {
                usageBar.append("█");
            } else {
                usageBar.append("░");
            }
        }
        return usageBar.toString();
    }
}
