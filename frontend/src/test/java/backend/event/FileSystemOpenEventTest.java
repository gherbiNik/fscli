package backend.event;

import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemOpenEventTest {

    @Mock
    private FileSystemModel modelMock;

    @InjectMocks
    private ClearEvent event;

    @Test
    public void constructor0() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new ClearEvent(null, null, null, null)
        );

        assertEquals("null source", e.getMessage());
    }

    @Test
    public void constructor1() {
        assertNotNull(event);
        assertNotNull(event.getSource());
        assertEquals(modelMock, event.getSource());
    }
}
