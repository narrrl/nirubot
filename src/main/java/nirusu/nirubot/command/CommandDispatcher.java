package nirusu.nirubot.command;

import java.util.ServiceLoader;

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
            if (g.getKey().equals(group)) {
                return g;
            }
        }

        throw new IllegalArgumentException();

    }
}
