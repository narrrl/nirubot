package nirusu.nirubot.core.help;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import nirusu.nirubot.core.help.CommandMeta.Metadata;
import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class HelpCreator {
    private final Map<String, Class<? extends BaseModule>> modulesMap;
    private final CommandMeta metadata = CommandMeta.getMetadataForCommands();
    private static final Metadata INVALID = new Metadata().setName("").setSyntax("")
                                            .setDescription("Command not found");

    public HelpCreator(@Nonnull Set<Class<? extends BaseModule>> modules) {
        modulesMap = new HashMap<>();
        for (Class<? extends BaseModule> module : modules) {
            modulesMap.put(module.getSimpleName(), module);
        }
    }


    public Metadata metadataForCommand(Class<? extends BaseModule> module, String key) {
        for (Method m : module.getMethods()) {
            if (commandContainsKey(m, key)) {
                return metadata.getMetadataForName(m.getName()).map(meta -> meta.setAliases(getAliasesForCommand(m))).orElse(INVALID);
            }
        }
        return INVALID;
    }

    private boolean commandContainsKey(Method m, String key) {
        if (m.isAnnotationPresent(Command.class)) {
            for (String keyOfCommand : m.getAnnotation(Command.class).key()) {
                if (keyOfCommand.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[] getAliasesForCommand(Method m) {
        return m.isAnnotationPresent(Command.class) ? m.getAnnotation(Command.class).key() : new String[] {};
    }

    public String listCommandsFor(Class<? extends BaseModule> module) {
        StringBuilder str = new StringBuilder();
        for (Method m : module.getMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                str.append(m.getName()).append(", ");
            }
        }
        return str.length() == 0 ? "No commands found" : str.substring(0, str.length() - 2);
    }

    public String listModules() {
        StringBuilder str = new StringBuilder();
        for (String moduleName : modulesMap.keySet()) {
            str.append(moduleName).append(", ");
        }
        return str.length() == 0 ? "No modules found" : str.substring(0, str.length() - 2);
    }

    public Optional<Class<? extends BaseModule>> getModuleWithName(String moduleName) {
        return Optional.ofNullable(modulesMap.get(moduleName));
    }
}
