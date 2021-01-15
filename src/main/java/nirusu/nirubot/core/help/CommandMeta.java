package nirusu.nirubot.core.help;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nirusu.nirubot.Nirubot;

/**
 * Helps to write and parse meta data about a command. It reads the metadata of
 * all commands from {@link nirusu.nirubot.core.help.command.yml}
 */
public class CommandMeta {
    private static final String ERROR_ON_FAILURE = "Couldn't parse commands.yaml";
    private static CommandMeta metaData;
    private List<Metadata> commands;

    public CommandMeta() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            commands = mapper.readValue(CommandMeta.class.getResourceAsStream("commands.yaml"),
                    new TypeReference<List<Metadata>>() {
                    });
        } catch (IOException e) {
            Nirubot.error(ERROR_ON_FAILURE, e);
            throw new IllegalArgumentException();
        }
    }

    public List<Metadata> getCommandsMetadata() {
        return Collections.unmodifiableList(this.commands);
    }

    public Optional<Metadata> getMetadataForName(String name) {
        for (Metadata m : commands) {
            if (m.name.equals(name)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public static CommandMeta getMetadataForCommands() {
        if (metaData == null) {
            metaData = new CommandMeta();
        }
        return metaData;
    }

    /**
     * This classes represent the meta data of a command. Its used to parse the
     * information from the yaml file in resources
     */
    public static final class Metadata {
        private String name;
        private String description;
        private String syntax;
        @JsonIgnore
        private String[] aliases;

        public Metadata() {
            // empty constructor for jackson
        };

        public String getSyntax() {
            return this.syntax;
        }

        public String getDescription() {
            return this.description;
        }

        public String getName() {
            return this.name;
        }

        public String getAliases() {
            if (aliases == null) return "";
            return Stream.of(this.aliases).collect(Collectors.joining(", "));
        }

        public Metadata setSyntax(String syntax) {
            this.syntax = syntax;
            return this;
        }

        public Metadata setDescription(String description) {
            this.description = description;
            return this;
        }

        public Metadata setName(String name) {
            this.name = name;
            return this;
        }

        public Metadata setAliases(String[] aliases) {
            this.aliases = aliases;
            return this;

        }
    }
}
