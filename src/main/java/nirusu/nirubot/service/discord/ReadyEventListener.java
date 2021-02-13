package nirusu.nirubot.service.discord;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import nirusu.nirubot.Nirubot;
import reactor.core.publisher.Mono;

public class ReadyEventListener implements EventListener<ReadyEvent> {

    @Override
    public Class<ReadyEvent> eventType() {
        return ReadyEvent.class;
    }

    @Override
    public Mono<Void> execute(ReadyEvent event) {
        Nirubot.info("Logging in as {}", event.getSelf().getUsername());
        return Mono.empty();
    }
    
}
