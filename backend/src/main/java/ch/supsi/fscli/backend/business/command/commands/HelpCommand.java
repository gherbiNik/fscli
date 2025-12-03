package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoArgumentsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoOptionsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.List;

public class HelpCommand extends AbstractValidatedCommand {

    // Questa è la lista che il CommandLoader ci passerà dopo aver creato tutto
    private List<ICommand> commands;

    public HelpCommand(FileSystemService fsService, String name, String synopsis, String description) {
        super(fsService, name, synopsis, description);
    }

    // Il metodo che userà il CommandLoader (tramite Reflection) per iniettare la lista
    public void setCommands(List<ICommand> commands) {
        this.commands = commands;
    }

    @Override
    protected CommandValidator getValidator() {
        return new NoOptionsValidator(getName())
                .and(new NoArgumentsValidator(getName()));
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        // Controllo se la lista è stata iniettata correttamente
        if (this.commands == null || this.commands.isEmpty()) {
            return CommandResult.error("help: no commands available (system error)");
        }

        BackendTranslator backendTranslator = BackendTranslator.getInstance();
        StringBuilder sb = new StringBuilder(backendTranslator.getString("commandList.title") + "\n");
        // Iteriamo sulla lista 'this.commands' invece di usare il vecchio 'container'
        for (ICommand cmd : this.commands) {
            sb.append(backendTranslator.getString(cmd.getSynopsis()))
                    .append(" : ")
                    .append(backendTranslator.getString(cmd.getDescription()))
                    .append("\n");
        }

        // Rimuovi l'ultimo \n
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return CommandResult.success(sb.toString());
    }
}