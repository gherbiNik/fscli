package ch.supsi.fscli.backend.dataAccess.filesystem;

import ch.supsi.fscli.backend.business.dto.IFsStateDto;

import java.io.File;

public interface ISaveData {
    void save(IFsStateDto iFsStateDto);
    void saveAs(IFsStateDto iFsStateDto, File file);
    IFsStateDto load(String fileName);

    String getCurrentFileAbsolutePath();
}
