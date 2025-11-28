package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.dto.IFsStateDto;

import java.io.File;

public interface ISaveDataService {
    void save(IFsStateDto iFsStateDto);
    void saveAs(IFsStateDto iFsStateDto, File file);
    IFsStateDto load(String fileName);
}
