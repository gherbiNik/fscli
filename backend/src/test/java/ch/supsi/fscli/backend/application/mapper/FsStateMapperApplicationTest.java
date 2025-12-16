package ch.supsi.fscli.backend.application.mapper;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.backend.business.dto.IFsStateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FsStateMapperApplicationTest {

    @Mock
    private IFsStateMapper fsStateMapperMock;

    @Mock
    private IFileSystemApplication fileSystemApplicationMock;

    @InjectMocks
    private FsStateMapperApplication fsStateMapperApplication;

    @Test
    void testToDTO() {
        fsStateMapperApplication.toDTO();
        verify(fsStateMapperMock).toDTO();
    }

    @Test
    void testFromDTO_CreatesFileSystem_WhenNotCreated() {
        // Scenario: Il file system NON esiste ancora
        when(fileSystemApplicationMock.isFileSystemCreated()).thenReturn(false);
        String fileName = "test.json";

        fsStateMapperApplication.fromDTO(fileName);

        // Verifica: deve aver chiamato createFileSystem() PRIMA di delegare al mapper
        verify(fileSystemApplicationMock).createFileSystem();
        verify(fsStateMapperMock).fromDTO(fileName);
    }

    @Test
    void testFromDTO_DoesNotCreateFileSystem_WhenAlreadyCreated() {
        // Scenario: Il file system esiste già
        when(fileSystemApplicationMock.isFileSystemCreated()).thenReturn(true);
        String fileName = "test.json";

        fsStateMapperApplication.fromDTO(fileName);

        // Verifica: NON deve chiamare createFileSystem()
        verify(fileSystemApplicationMock, never()).createFileSystem();
        verify(fsStateMapperMock).fromDTO(fileName);
    }

    @Test
    void testToDTOas() {
        File mockFile = new File("test.json");
        fsStateMapperApplication.toDTOas(mockFile);
        verify(fsStateMapperMock).toDTOas(mockFile);
    }

    @Test
    void testGetCurrentFileAbsolutePath() {
        String expectedPath = "/tmp/path";
        when(fsStateMapperMock.getCurrentFileAbsolutePath()).thenReturn(expectedPath);

        String result = fsStateMapperApplication.getCurrentFileAbsolutePath();

        assertEquals(expectedPath, result);
        verify(fsStateMapperMock).getCurrentFileAbsolutePath();
    }
}