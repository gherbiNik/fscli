package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandResultTest {
    @Test
    void testSuccessFactory() {
        String output = "Command executed successfully";
        CommandResult result = CommandResult.success(output);
        assertTrue(result.isSuccess());
        assertEquals(output, result.getOutput());
        assertNull(result.getError());
    }

    @Test
    void testErrorFactory() {
        String error = "Command failed";
        CommandResult result = CommandResult.error(error);
        assertFalse(result.isSuccess());
        assertNull(result.getOutput());
        assertEquals(error, result.getError());
    }
}
