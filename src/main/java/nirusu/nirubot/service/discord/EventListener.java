package nirusu.nirubot.service.discord;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {

    Class<T> eventType();

    Mono<Void> execute(T event);
    
}
