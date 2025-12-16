package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.dto.IFsStateDto;
import ch.supsi.fscli.backend.dataAccess.filesystem.ISaveData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveDataServiceTest {

    @Mock
    private ISaveData saveDataDAO; // Mock del DAO sottostante (JacksonSaveDataService)

    @InjectMocks
    private SaveDataService saveDataService;

    @Test
    void testSave() {
        IFsStateDto dtoMock = mock(IFsStateDto.class);

        saveDataService.save(dtoMock);

        verify(saveDataDAO).save(dtoMock);
    }

    @Test
    void testSaveAs() {
        IFsStateDto dtoMock = mock(IFsStateDto.class);
        File fileMock = new File("backup.json");

        saveDataService.saveAs(dtoMock, fileMock);

        verify(saveDataDAO).saveAs(dtoMock, fileMock);
    }

    @Test
    void testLoad() {
        String filename = "state.json";
        IFsStateDto expectedDto = mock(IFsStateDto.class);
        when(saveDataDAO.load(filename)).thenReturn(expectedDto);

        IFsStateDto result = saveDataService.load(filename);

        assertEquals(expectedDto, result);
        verify(saveDataDAO).load(filename);
    }

    @Test
    void testGetCurrentFileAbsolutePath() {
        String expectedPath = "/home/user/fscli/data.json";
        when(saveDataDAO.getCurrentFileAbsolutePath()).thenReturn(expectedPath);

        String result = saveDataService.getCurrentFileAbsolutePath();

        assertEquals(expectedPath, result);
        verify(saveDataDAO).getCurrentFileAbsolutePath();
    }
}