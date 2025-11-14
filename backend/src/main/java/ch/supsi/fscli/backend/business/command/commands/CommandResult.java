package ch.supsi.fscli.backend.business.command.commands;

public class CommandResult {
    private final boolean success;
    private final String output;
    private final String error;

    private CommandResult(boolean success, String output, String error) {
        this.success = success;
        this.output = output;
        this.error = error;
    }

    public static CommandResult success(String output) {
        return new CommandResult(true, output, null);
    }

    public static CommandResult error(String error) {
        return new CommandResult(false, null, error);
    }

    public boolean isSuccess() { return success; }
    public String getOutput() { return output; }
    public String getError() { return error; }
}
