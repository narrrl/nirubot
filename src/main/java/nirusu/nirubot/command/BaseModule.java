package nirusu.nirubot.command;

public abstract class BaseModule {
    protected CommandContext ctx;

    public void setCommandContext(final CommandContext ctx) {
        this.ctx = ctx;
    }
    
}
