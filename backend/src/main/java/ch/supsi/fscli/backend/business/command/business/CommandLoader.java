package ch.supsi.fscli.backend.business.command.business;

import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.dataAccess.ICommandDAO;
import ch.supsi.fscli.backend.dataAccess.JsonCommandDTO;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CommandLoader {

    private final ICommandDAO commandDAO;
    private final IFileSystemService fileSystemService;

    // Package dove risiedono le classi dei comandi
    private static final String COMMANDS_PACKAGE = "ch.supsi.fscli.backend.business.command.commands.";

    public CommandLoader(ICommandDAO commandDAO, IFileSystemService fileSystemService) {
        this.commandDAO = commandDAO;
        this.fileSystemService = fileSystemService;
    }

    public List<ICommand> loadCommands() {
        List<JsonCommandDTO> dtos = commandDAO.getAllCommands();
        List<ICommand> loadedCommands = new ArrayList<>();

        for (JsonCommandDTO dto : dtos) {
            try {
                String fullClassName = COMMANDS_PACKAGE + dto.getClassName();
                Class<?> commandClass = Class.forName(fullClassName);

                // Cerchiamo SEMPRE il costruttore standard a 4 parametri
                Constructor<?> constructor = commandClass.getConstructor(
                        IFileSystemService.class,
                        String.class,
                        String.class,
                        String.class
                );

                ICommand commandInstance = (ICommand) constructor.newInstance(
                        this.fileSystemService,
                        dto.getCommandName(),
                        dto.getSynopsisKey(),
                        dto.getDescriptionKey()
                );

                loadedCommands.add(commandInstance);

            } catch (Exception e) {
                System.err.println("Errore caricamento comando: " + dto.getClassName());
                e.printStackTrace();
            }
        }

        // Ora che la lista loadedCommands è piena, cerchiamo HelpCommand e gli diamo la lista
        for (ICommand cmd : loadedCommands) {
            if (cmd.getClass().getSimpleName().equals("HelpCommand")) {
                try {
                    cmd.getClass().getMethod("setCommands", List.class).invoke(cmd, loadedCommands);

                    System.out.println("Lista comandi iniettata in HelpCommand.");
                } catch (Exception e) {
                    System.err.println("Impossibile configurare HelpCommand: " + e.getMessage());
                }
            }
        }

        return loadedCommands;
    }
}