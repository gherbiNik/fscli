package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.dto.IFsStateDto;
import ch.supsi.fscli.backend.dataAccess.filesystem.ISaveData;
import ch.supsi.fscli.backend.dataAccess.filesystem.JacksonSaveDataService;
import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;

@Singleton
public class SaveDataService implements ISaveDataService{
    private final ISaveData jacksonSaveDataService;

    @Inject
    public SaveDataService(ISaveData jacksonSaveDataService) {
        this.jacksonSaveDataService = jacksonSaveDataService;
    }

    @Override
    public void save(IFsStateDto iFsStateDto) {
        jacksonSaveDataService.save(iFsStateDto);
    }

    @Override
    public void saveAs(IFsStateDto iFsStateDto, File file) {
        jacksonSaveDataService.saveAs(iFsStateDto, file);
    }

    @Override
    public IFsStateDto load(String fileName) {
        return jacksonSaveDataService.load(fileName);
    }

    @Override
    public String getCurrentFileAbsolutePath() {
        return jacksonSaveDataService.getCurrentFileAbsolutePath();
    }
}


