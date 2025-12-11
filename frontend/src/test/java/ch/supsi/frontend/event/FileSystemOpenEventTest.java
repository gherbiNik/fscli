package ch.supsi.frontend.event;

import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.event.FileSystemOpenEvent;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.FsStateMapperModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class FileSystemOpenEventTest {

    @Mock
    private FsStateMapperModel modelMock;

    @InjectMocks
    private FileSystemOpenEvent event;

    @Test
    public void constructor0() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new FileSystemOpenEvent(null, null, null, null)
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
