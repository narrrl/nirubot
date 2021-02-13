package nirusu.nirubot.model.tictactoe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import nirusu.nirubot.core.GuildManager;
import nirusu.nirubot.model.tictactoe.Player.Symbol;
import nirusu.nirucmd.CommandContext;

public class TicTacToeHandler {
    // maps channel id to a handler
    private static final Map<Snowflake, TicTacToeHandler> games;

    private static final String TOO_MANY_USER_ERROR = "You can challenge only one player at a time";
    private static final String NO_USER_MENTIONED_ERROR = "You have to mention another player to start a game of tic tac toe!";
    private static final String CHANNEL_NOT_FOUND_ERROR = "Couldn't find current channel!";
    private static final String AUTHOR_NOT_FOUND_ERROR = "Couldn't get author of the message. Try again or contact a bot administrator";
    private static final String GAME_IS_RUNNING_ERROR = "A game is currently running in this channel!";
    private static final String GUILD_NOT_FOUND_ERROR = "Couldn't get guild for this channel!";

    private static final String GAME_REQUEST = "%s challenged you to a game of tic tac toe, %s! Type `%sttt accept` to start the game!";

    static {
        games = new HashMap<>();
    }

    private final Map<Snowflake, Player> players;
    private final TicTacToe game;
    private boolean isRunning;

    private TicTacToeHandler(Snowflake one, Snowflake two, int boardSize) {
        players = new HashMap<>();

        players.put(one, new Player(Symbol.ONE));
        players.put(two, new Player(Symbol.TWO));

        game = new TicTacToe(players.get(one), players.get(two), boardSize);
        isRunning = false;
    }

    public static Optional<TicTacToeHandler> of(Snowflake channelId) {
        return Optional.ofNullable(games.get(channelId));
    }

    public static TicTacToeHandler createGame(CommandContext ctx) {
        return ctx.getEvent().getMessage().getUserMentions().collectList().blockOptional().map(users -> {
            if (users.size() != 1) {
                throw new TicTacToeException(TOO_MANY_USER_ERROR);
            }

            User two = users.get(0);

            User one = ctx.getAuthor().orElseThrow(() -> new TicTacToeException(AUTHOR_NOT_FOUND_ERROR));

            Channel ch = ctx.getChannel().orElseThrow(() -> new TicTacToeException(CHANNEL_NOT_FOUND_ERROR));

            Guild g = ctx.getGuild().orElseThrow(() -> new TicTacToeException(GUILD_NOT_FOUND_ERROR));

            GuildManager m = GuildManager.of(g.getId());

            if (games.containsKey(ch.getId())) {
                throw new TicTacToeException(GAME_IS_RUNNING_ERROR);
            }

            TicTacToeHandler handler = new TicTacToeHandler(one.getId(), two.getId(), 3);
            games.put(ch.getId(), handler);

            ctx.reply(String.format(GAME_REQUEST, one.getMention(), two.getMention(), m.prefix()));

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                if (!handler.isRunning) {
                    games.remove(ch.getId());
                    ctx.reply(String.format("%s didn't accept in time :(", two.getMention()));
                }
            }, 30, TimeUnit.SECONDS);
            return handler;
        }).orElseThrow(() -> new TicTacToeException(NO_USER_MENTIONED_ERROR));
    }

    public static boolean acceptGame(CommandContext ctx) {
        return ctx.getArgs().map(args -> {

            return ctx.getChannel().map(ch -> {
                TicTacToeHandler handler = games.get(ch.getId());
                if (handler == null) {
                    return false;
                }
                boolean accepted = handler.accept(ctx.getAuthor().map(User::getId).orElse(null));
                if (accepted) {
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.schedule(() -> {
                        if (games.containsKey(ch.getId())) {
                            games.remove(ch.getId());
                            ctx.reply("Game has been cancelled!");
                        }
                    }, 15, TimeUnit.MINUTES);
                }
                return accepted;
            }).orElse(false);

        }).orElse(false);
    }

    private boolean accept(Snowflake user) {
        boolean accepted = players.containsKey(user) && players.get(user).getSymbol().equals(Symbol.TWO);
        if (accepted) {
            isRunning = true;
        }
        return accepted;
    }

    public void makeTurn(CommandContext ctx) {
        if (!isRunning) {
            ctx.reply("Game is not running!");
            return;
        }
        ctx.getArgs().ifPresent(args -> {
            if (args.size() != 3) {
                return;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(args.get(1));
                y = Integer.parseInt(args.get(2));
            } catch (NumberFormatException e) {
                ctx.reply("Coordinates must be whole numbers!");
                return;
            }

            ctx.getAuthor().ifPresent(u -> players.computeIfPresent(u.getId(), (key, p) -> {
                ctx.reply(game.makeTurn(x, y, p));
                return p;
            }));

        });

        ctx.getChannel().ifPresent(ch -> {
            if (game.hasEnded()) {
                games.remove(ch.getId());
            }
        });
    }

    public enum TicTacToeCommand {
        START {
            @Override
            public void exec(CommandContext ctx) {
                try {
                    TicTacToeHandler.createGame(ctx);
                } catch (TicTacToeException e) {
                    ctx.reply(e.getMessage());
                }
            }
        },
        ACCEPT {
            @Override
            public void exec(CommandContext ctx) {
                if (TicTacToeHandler.acceptGame(ctx)) {
                    ctx.reply("May the game begin!");
                }
            }
        },
        PUT {
            @Override
            public void exec(CommandContext ctx) {
                ctx.getChannel().ifPresent(ch -> TicTacToeHandler.of(ch.getId()).ifPresent(h -> 
                        h.makeTurn(ctx)
                ));
            }
        },
        INVALID {
            @Override
            public void exec(CommandContext ctx) {
                ctx.reply("Unknown Command!");
            }
        };

        public abstract void exec(CommandContext ctx);

        public static TicTacToeCommand get(String key) {
            for (TicTacToeCommand cmd : values()) {
                if (cmd.name().equalsIgnoreCase(key)) {
                    return cmd;
                }
            }
            return INVALID;
        }
    }
}
