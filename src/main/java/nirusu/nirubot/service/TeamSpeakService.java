package nirusu.nirubot.service;

import nirusu.nirubot.Nirubot;

public class TeamSpeakService implements NiruService {

    @Override
    public void shutdown() {
        Nirubot.info("TeamSpeak Service is shutting down!");
    }

    @Override
    public void run() {
        Nirubot.info("TeamSpeak Service is starting!");

    }
    
}
