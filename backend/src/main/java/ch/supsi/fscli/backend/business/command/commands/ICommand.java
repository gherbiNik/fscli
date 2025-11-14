package ch.supsi.fscli.backend.business.command.commands;

public interface ICommand {
    CommandResult execute(CommandContext context);
    String getName();
    String getSynopsis();
    String getDescription();
}
