package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.dataAccess.ICommandDAO;
import ch.supsi.fscli.backend.dataAccess.JsonCommandDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Abilita Mockito per questa classe
class CommandLoaderTest {

    // Creo il mock per l'accesso ai dati (chi ci dà la lista dei comandi JSON)
    @Mock
    private ICommandDAO commandDAO;

    // Creo il mock per il servizio file system (necessario al costruttore dei comandi)
    @Mock
    private IFileSystemService fileSystemService;

    // Creo l'istanza della classe da testare e iniettiamo automaticamente i mock sopra
    @InjectMocks
    private CommandLoader commandLoader;

    @Test
    void testLoadCommands_Success() {
        // 1. PREPARAZIONE
        // Creiamo un DTO finto che simula un comando presente nel JSON
        JsonCommandDTO fakeDto = new JsonCommandDTO();

        fakeDto.setClassName("HelpCommand");
        fakeDto.setCommandName("help");
        fakeDto.setSynopsisKey("command.help.synopsis");
        fakeDto.setDescriptionKey("command.help.desc");

        // Istruiamo il Mock: quando chiamano getAllCommands, restituisci una lista con il nostro DTO
        OngoingStubbing<List<JsonCommandDTO>> listOngoingStubbing = when(commandDAO.getAllCommands()).thenReturn(Collections.singletonList(fakeDto));

        // 2. ESECUZIONE
        List<ICommand> result = commandLoader.loadCommands();

        // 3. VERIFICA
        // Verifichiamo che la lista non sia vuota e contenga il comando caricato
        assertFalse(result.isEmpty(), "La lista dei comandi non dovrebbe essere vuota");
        assertEquals(1, result.size(), "Dovrebbe esserci esattamente un comando caricato");

        // Verifico che il comando caricato sia dell'istanza giusta
        assertEquals("HelpCommand", result.get(0).getClass().getSimpleName());
    }

    @Test
    void testLoadCommands_WithInvalidClass_ShouldSkipCommand() {
        // PREPARAZIONE
        // Creo un DTO con un nome di classe che NON esiste
        JsonCommandDTO invalidDto = new JsonCommandDTO();
        invalidDto.setClassName("ClasseCheNonEsiste");
        invalidDto.setCommandName("fake");

        // Istruisco il Mock
        when(commandDAO.getAllCommands()).thenReturn(Collections.singletonList(invalidDto));

        // ESECUZIONE
        List<ICommand> result = commandLoader.loadCommands();

        // VERIFICA
        // Il loader ha catturato l'eccezione internamente, quindi non deve essere esploso.
        // La lista risultante deve essere vuota perché l'unico comando era invalido.
        assertTrue(result.isEmpty(), "La lista dovrebbe essere vuota se la classe non viene trovata");
    }

}
