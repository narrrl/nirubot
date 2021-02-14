package nirusu.nirubot.service.discord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.qos.logback.core.net.server.Client;
import discord4j.common.close.CloseException;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.discordjson.json.ApplicationInfoData;
import discord4j.discordjson.json.UserData;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.response.ResponseFunction;
import discord4j.store.api.mapping.MappingStoreService;
import nirusu.nirubot.Nirubot;
import nirusu.nirubot.core.Config;
import nirusu.nirubot.model.DiscordUtil;
import nirusu.nirubot.model.youtubedl.YoutubeDLHandler;
import nirusu.nirubot.service.NiruService;
import reactor.core.publisher.Mono;

public class DiscordService implements NiruService {
    private final Config conf;
    private GatewayDiscordClient gateway;

    public DiscordService() {
        conf = Nirubot.getConfig();
    }

    private <T extends Event> void register(GatewayDiscordClient gateway, EventListener<T> eventListener) {
        gateway.getEventDispatcher().on(eventListener.eventType())
                .flatMap(event -> eventListener.execute(event)
                        .timeout(Duration.ofMinutes(5),
                                Mono.error(new RuntimeException(String.format("%s timed out", event))))
                        .onErrorResume(err -> Mono.fromRunnable(() -> Nirubot.error("An exception occured {}", err))))
                .subscribe(null, Nirubot::error);
    }

    @Override
    public synchronized void shutdown() {
        Nirubot.info("Discord listener is shutting down");
        YoutubeDLHandler.getInstance().shutdown();
        gateway.logout()
            .then(Mono.fromRunnable(
                () -> Nirubot.info("Discord listener was shutdown"))
            );
    }

    @Override
    public void start() {
        final DiscordClient client = DiscordClient.builder(conf.getToken())
                .onClientResponse(ResponseFunction.emptyIfNotFound()).build();

        client.getApplicationInfo().map(ApplicationInfoData::owner).map(UserData::id).map(Snowflake::asLong)
                .doOnNext(Nirubot::setOwner).block();
        List<String> status = new ArrayList<>(List.of(conf.getActivityType()));
        status.addAll(Arrays.asList(conf.getActivity().split(" ")));
        Nirubot.info("Connecting to discord");
        client.gateway().setAwaitConnections(false)
                .setEnabledIntents(IntentSet.of(
                    Intent.GUILDS, 
                    Intent.GUILD_MEMBERS, 
                    Intent.GUILD_VOICE_STATES,
                    Intent.GUILD_MESSAGES, 
                    Intent.GUILD_MESSAGE_REACTIONS, 
                    Intent.DIRECT_MESSAGES))
                .setStoreService(MappingStoreService.create())
                .setInitialStatus(ignored -> Presence.online(DiscordUtil.getActivityUpdateRequest(status)
                    .orElse(Activity.listening(String.format("%shelp", conf.getPrefix())))))
                .withGateway(gate -> {
                    this.gateway = gate;
                    Nirubot.info("Starting Discord-Events");
                    register(gate, new MessageCreateListener());
                    register(gate, new ReadyEventListener());

                    Nirubot.info("DiscordService is ready");
                    return gate.onDisconnect();
                }).block();
    }


}
