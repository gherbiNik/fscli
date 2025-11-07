package ch.supsi.fscli.backend.business.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedCommandTest {
    ParsedCommand parsed;

    @BeforeEach
    void setup() {
        this.parsed = new ParsedCommand();
    }

    @Test
    void testDefaultConstructor() {
        assertNull(parsed.getCommandName());
        assertTrue(parsed.getArguments().isEmpty());
        assertTrue(parsed.getOptions().isEmpty());
    }

    @Test
    void testSetAndGetCommandName() {
        String name = "ls";
        parsed.setCommandName(name);
        assertEquals(name, parsed.getCommandName());
    }

    @Test
    void testSetAndGetArguments() {
        List<String> args = new ArrayList<>();
        args.add("file.txt");
        args.add("dir");
        parsed.setArguments(args);
        assertEquals(args, parsed.getArguments());
        assertEquals(2, parsed.getArguments().size());
    }

    @Test
    void testSetAndGetOptions() {
        Map<String, String> opts = new HashMap<>();
        opts.put("-l", "");
        opts.put("-a", "");
        parsed.setOptions(opts);
        assertEquals(opts, parsed.getOptions());
        assertEquals(2, parsed.getOptions().size());
    }

    // Test on the exception:
    @Test
    void testConstructorAndMessage() {
        String message = "Test message";
        InvalidCommandException exception = new InvalidCommandException(message);
        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }
}
