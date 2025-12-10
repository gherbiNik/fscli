package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator; // Import necessario
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class HelpCommandTest {

    @Mock
    IFileSystemService fsServiceMock;

    @Mock
    BackendTranslator translatorMock;

    @Mock
    ICommand commandMock1;

    @Mock
    ICommand commandMock2;

    HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        // Configuriamo il comportamento base del traduttore
        // Quando gli chiediamo una stringa, ci ridà la chiave stessa (es. "title" -> "title")
        // Usiamo 'lenient()' perché Mockito potrebbe lamentarsi se non usiamo il mock in tutti i test
        Mockito.lenient().when(translatorMock.getString(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        // Iniettiamo il mock nel campo statico della classe astratta
        AbstractValidatedCommand.setTranslator(translatorMock);

        // Istanziamo la classe da testare
        helpCommand = new HelpCommand(fsServiceMock, "help", "synopsis", "desc");
    }

    @Test
    void testExecuteCommand_Success() {
        // Stubbing dei comandi
        Mockito.when(commandMock1.getSynopsis()).thenReturn("cmd1");
        Mockito.when(commandMock1.getDescription()).thenReturn("desc1");

        Mockito.when(commandMock2.getSynopsis()).thenReturn("cmd2");
        Mockito.when(commandMock2.getDescription()).thenReturn("desc2");

        // Set commands
        helpCommand.setCommands(List.of(commandMock1, commandMock2));

        // Context vuoto
        CommandContext context = new CommandContext(null, Collections.emptyList(), Collections.emptyList());

        // Execute
        CommandResult result = helpCommand.execute(context);

        // Assert
        assertTrue(result.isSuccess());
        // Nota: ora l'output conterrà le chiavi non tradotte (es. "commandList.title") + i comandi
        String output = result.getOutput();
        assertTrue(output.contains("cmd1 : desc1"));
        assertTrue(output.contains("cmd2 : desc2"));
    }

    @Test
    void testExecuteCommand_WithNullList_ReturnsError() {
        helpCommand.setCommands(null);

        CommandContext context = new CommandContext(null, Collections.emptyList(), Collections.emptyList());

        CommandResult result = helpCommand.execute(context);

        // Deve fallire
        assertTrue(!result.isSuccess());
    }
}