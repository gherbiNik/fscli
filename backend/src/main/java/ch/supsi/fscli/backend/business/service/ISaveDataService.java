package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.dto.IFsStateBusiness;

public interface ISaveDataService {
    void save(IFsStateBusiness iFsStateBusiness);
    void saveAs(IFsStateBusiness iFsStateBusiness);
    IFsStateBusiness load(String fileName);
}
