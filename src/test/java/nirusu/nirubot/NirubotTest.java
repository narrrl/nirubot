package nirusu.nirubot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import nirusu.nirubot.core.help.CommandMeta;
import nirusu.nirubot.core.help.CommandMeta.Metadata;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class NirubotTest {

    @Test
    public void testCommandMetadata() {
        Nirubot nirubot = Nirubot.getNirubot();
        CommandMeta metadata = CommandMeta.getMetadataForCommands();

        List<Metadata> listOfData = metadata.getCommandsMetadata();

        for (Class<? extends BaseModule> c : nirubot.getDispatcher().getModules()) {
            for (Method m : c.getMethods()) {
                boolean methodHasMetadata = false;
                if (m.isAnnotationPresent(Command.class)) {
                    for (Metadata meta : listOfData) {
                        if (meta.getName().equals(m.getName())) {
                            methodHasMetadata = true;
                        }
                    }
                    assertTrue(methodHasMetadata);
                }

            }
        }

    }
}
