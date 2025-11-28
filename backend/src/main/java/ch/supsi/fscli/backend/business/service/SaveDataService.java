package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.dto.IFsStateDto;
import ch.supsi.fscli.backend.dataAccess.filesystem.JacksonSaveDataService;
import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;

import java.io.File;

public class SaveDataService implements ISaveDataService{
    private static SaveDataService myself = null;
    private JacksonSaveDataService jacksonSaveDataService;

    private SaveDataService() {
    }

    public static SaveDataService getInstance(PreferenceDAO preferenceDAO,JacksonSaveDataService jacksonSaveDataService) {
        if (myself == null) {
            myself = new SaveDataService();
            myself.jacksonSaveDataService = jacksonSaveDataService;
        }
        return myself;
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
}


