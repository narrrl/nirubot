package nirusu.nirubot.service.teamspeak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import nirusu.nirubot.Nirubot;
import nirusu.nirubot.model.youtubedl.InvalidYoutubeDlException;
import nirusu.nirubot.model.youtubedl.YoutubeDl;
import nirusu.nirubot.service.NiruService;

public class TeamSpeakService implements NiruService {

    @Override
    public void shutdown() {
        Nirubot.info("TeamSpeak Service is shutting down!");
    }

    @Override
    public void run() {
        Nirubot.info("TeamSpeak Service is starting!");
        final TS3Config config = new TS3Config();
        config.setHost(Nirubot.getConfig().getTS3Ip());
        final TS3Query query = new TS3Query(config);
        query.connect();

        final TS3Api api = query.getApi();

        api.login(Nirubot.getConfig().getTS3Login(), Nirubot.getConfig().getTS3Password());

        api.selectVirtualServerById(1);

        api.setNickname("Nirubot");
        api.moveQuery(api.getChannelByNameExact(Nirubot.getConfig().getTS3Channel(), true));

        api.registerAllEvents();
        api.addTS3Listeners(new TS3Listener() {

            @Override
            public void onTextMessage(TextMessageEvent e) {
                String input = e.getMessage();

                if (input.startsWith("yt")) {

                    String[] splitted = input.replace("[URL]", "")
                        .replace("[/URL]", "").split("\\s+");

                    try {
                        File file = new YoutubeDl(splitted).getFile();
                        api.uploadFile(new FileInputStream(file), file.length(),
                                file.getName(), false,
                                api.getChannelByNameExact(Nirubot.getConfig().getTS3Channel(), true).getId());
                        api.sendChannelMessage("File uploaded!");
                    } catch (InvalidYoutubeDlException err) {
                        if (err.getMessage() != null) {
                            api.sendChannelMessage("Error" + err.getMessage());
                        }
                    } catch (FileNotFoundException e1) {
                        api.sendChannelMessage("Error" + e1.getMessage());
                    }

                }
            }

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onServerEdit(ServerEditedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelEdit(ChannelEditedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelCreate(ChannelCreateEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelDeleted(ChannelDeletedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelMoved(ChannelMovedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                throw new UnsupportedOperationException();
            }

        });
    }
}
