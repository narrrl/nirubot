package nirusu.nirubot.service;

import nirusu.nirubot.Nirubot;

public class TeamSpeakService implements NiruService {

    @Override
    public boolean shutdown() {
        Nirubot.info("TeamSpeak Service is shutting down!");
        return true;
    }
    
}
