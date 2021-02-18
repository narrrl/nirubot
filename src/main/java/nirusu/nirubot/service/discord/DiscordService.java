package nirusu.nirubot.service.discord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import discord4j.common.retry.ReconnectOptions;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.retriever.EntityRetrievalStrategy;
import discord4j.core.shard.MemberRequestFilter;
import discord4j.core.shard.ShardingStrategy;
import discord4j.discordjson.json.ApplicationInfoData;
import discord4j.discordjson.json.UserData;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.response.ResponseFunction;
import discord4j.store.api.mapping.MappingStoreService;
import discord4j.voice.VoiceReactorResources;
import discord4j.voice.VoiceServerOptions;
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
                .subscribe(ignored -> {}, Nirubot::error);
    }

    @Override
    public synchronized void shutdown() {
        Nirubot.info("Discord listener is shutting down");
        YoutubeDLHandler.getInstance().shutdown();
        gateway.logout()
            .doAfterTerminate(
                () -> Nirubot.info("Discord listener was shutdown")
            ).then();
    }

    @Override
    public void run() {
        final DiscordClient client = DiscordClient.builder(conf.getToken())
                .onClientResponse(ResponseFunction.emptyIfNotFound()).build();

        client.getApplicationInfo().map(data -> data.owner().id()).map(Snowflake::asLong)
                .doOnNext(Nirubot::setOwner).block();
        List<String> status = new ArrayList<>(List.of(conf.getActivityType()));
        status.addAll(Arrays.asList(conf.getActivity().split(" ")));
        Nirubot.info("Connecting to discord");
        client.gateway().setAwaitConnections(false)
                .setEnabledIntents(IntentSet.all())
                .setSharding(ShardingStrategy.recommended())
                .setInitialStatus(ignored -> Presence.online(DiscordUtil.getActivityUpdateRequest(status)
                    .orElse(Activity.listening(String.format("%shelp", conf.getPrefix())))))
                .setMemberRequestFilter(MemberRequestFilter.none())
                .withGateway(gate -> {
                    this.gateway = gate;
                    register(gate, new MessageCreateListener());
                    register(gate, new ReadyEventListener());

                    return gate.onDisconnect();
                }).block();
    }
}
