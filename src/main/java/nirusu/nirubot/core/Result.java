package nirusu.nirubot.core;

import javax.annotation.Nonnull;

public class Result {

    private final String output;

    private final ResultType type;

    public Result(@Nonnull String output, @Nonnull ResultType type) {

        this.output = output;
        this.type = type;

    }

    public ResultType getType() {
        return this.type;
    }

    public String getOutput() {
        return this.output;
    }

    public enum ResultType {
        SUCCESS, FAILURE
    }
}
