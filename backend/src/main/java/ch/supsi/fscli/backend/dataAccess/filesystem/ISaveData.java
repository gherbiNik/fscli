package ch.supsi.fscli.backend.dataAccess.filesystem;

import ch.supsi.fscli.backend.business.dto.IFsStateBusiness;

public interface ISaveData {
    void save(IFsStateBusiness iFsStateBusiness);
    void saveAs(IFsStateBusiness iFsStateBusiness);
    IFsStateBusiness load(String fileName);
}
