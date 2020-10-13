package nirusu.nirubot.command;

public interface IPrivateCommand extends ICommand {

    public void execute(final PrivateCommandContext ctx);
}
