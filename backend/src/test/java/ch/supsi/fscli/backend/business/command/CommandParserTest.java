package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.business.InvalidCommandException;
import ch.supsi.fscli.backend.business.command.business.ParsedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {
    CommandParser parser;

    @BeforeEach
    void setup(){
        this.parser = CommandParser.getInstance();
    }

    @Test
    void testGetInstance() {
        CommandParser parser1 = CommandParser.getInstance();
        CommandParser parser2 = CommandParser.getInstance();
        assertNotNull(parser1);
        assertSame(parser1, parser2); // Singleton check
    }

    @Test
    void testParseEmptyCommand() {
        assertThrows(InvalidCommandException.class, () -> parser.parse(""));
        assertThrows(InvalidCommandException.class, () -> parser.parse(null));
        assertThrows(InvalidCommandException.class, () -> parser.parse("   "));
    }

    @Test
    void testParseSimpleCommandNoArgs() throws InvalidCommandException {
        ParsedCommand parsed = parser.parse("ls");
        assertEquals("ls", parsed.getCommandName());
        assertTrue(parsed.getArguments().isEmpty());
        assertTrue(parsed.getOptions().isEmpty());
    }

    @Test
    void testParseCommandWithArguments() throws InvalidCommandException {
        ParsedCommand parsed = parser.parse("cd dir1 dir2");
        assertEquals("cd", parsed.getCommandName());
        assertEquals(2, parsed.getArguments().size());
        assertEquals("dir1", parsed.getArguments().get(0));
        assertEquals("dir2", parsed.getArguments().get(1));
        assertTrue(parsed.getOptions().isEmpty());
    }

    @Test
    void testParseCommandWithOptions() throws InvalidCommandException {
        ParsedCommand parsed = parser.parse("ls -l -a");
        assertEquals("ls", parsed.getCommandName());
        assertTrue(parsed.getArguments().isEmpty());
        assertEquals(2, parsed.getOptions().size());
        assertTrue(parsed.getOptions().containsKey("-l"));
        assertTrue(parsed.getOptions().containsKey("-a"));
        assertEquals("", parsed.getOptions().get("-l"));
    }

    @Test
    void testParseCommandWithMixedArgsAndOptions() throws InvalidCommandException {
        // this command does not exists. used just as test
        ParsedCommand parsed = parser.parse("ls file1 file2 -a -b");
        assertEquals("ls", parsed.getCommandName());
        assertEquals(2, parsed.getArguments().size());
        assertEquals("file1", parsed.getArguments().get(0));
        assertEquals("file2", parsed.getArguments().get(1));
        assertEquals(2, parsed.getOptions().size());
        assertTrue(parsed.getOptions().containsKey("-a"));
        assertTrue(parsed.getOptions().containsKey("-b"));
    }

    @Test
    void testParseCommandWithSpaces() throws InvalidCommandException {
        ParsedCommand parsed = parser.parse("  mkdir  newdir  ");
        assertEquals("mkdir", parsed.getCommandName());
        assertEquals(1, parsed.getArguments().size());
        assertEquals("newdir", parsed.getArguments().get(0));
        assertEquals(0, parsed.getOptions().size());
    }
}
