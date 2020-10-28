package nirusu.nirubot.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import nirusu.nirubot.Nirubot;

public class CommandDispatcher {

    private CommandDispatcher() { throw new IllegalAccessError(); }

    /**
     * finds the command group 
     * @param group
     * @return
     * @throws IllegalArgumentException
     */
    public static ICommand getICommand(final String group) throws IllegalArgumentException {
        Iterable<ICommand> groups = ServiceLoader.load(ICommand.class);

        for (ICommand g : groups) {
            if (g.getKey().equals(group) || g.alias().stream().anyMatch(pre -> pre.equals(group))) {
                return g;
            }
        }

        throw new IllegalArgumentException();

    }

    public static IPrivateCommand getIPrivateCommand(final String group) throws IllegalArgumentException {
        Iterable<IPrivateCommand> groups = ServiceLoader.load(IPrivateCommand.class);

        for (IPrivateCommand g : groups) {
            if (g.getKey().equals(group) || g.alias().stream().anyMatch(pre -> pre.equals(group))) {
                return g;
            }
        }

        throw new IllegalArgumentException();
    }

    public static void checkForDuplicateAlias() {
        Iterable<ICommand> groups = ServiceLoader.load(ICommand.class);

        ArrayList<String> alias = new ArrayList<>();

        for (ICommand g : groups) {
            g.alias().forEach(alias::add);
        }

        int offset = 0;
        Set<String> duplicates = new HashSet<>();
        for (String alias1 : alias) {
            for (String alias2 : alias) {
                if (alias1.equals(alias2)) {
                    offset++;
                }
            }
            if (offset > 1 && !duplicates.contains(alias1)) {
                duplicates.add(alias1);
                Nirubot.warning(alias1 + " is a duplicate alias");
            }
            offset = 0;
        }

    }
}
