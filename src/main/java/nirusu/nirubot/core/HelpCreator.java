package nirusu.nirubot.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import nirusu.nirucmd.BaseModule;
import nirusu.nirucmd.annotation.Command;

public class HelpCreator {
    private final Map<String, Class<? extends BaseModule>> modulesMap;

    public HelpCreator(@Nonnull Set<Class<? extends BaseModule>> modules) {
        modulesMap = new HashMap<>();
        for (Class<? extends BaseModule> module : modules) {
            modulesMap.put(module.getSimpleName().replace("Module", ""), module);
        }
    }


    public String stringOfCommand(Class<? extends BaseModule> module, String key) {
        for (Method m : module.getMethods()) {
            // TODO: implement with yml files
        }
        return "Not implemented";
    }

    public String listCommandsFor(Class<? extends BaseModule> module) {
        StringBuilder str = new StringBuilder();
        for (Method m : module.getMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                str.append(m.getAnnotation(Command.class).key()[0]).append(", ");
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
