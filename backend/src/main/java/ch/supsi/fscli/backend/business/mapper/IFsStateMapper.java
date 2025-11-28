package ch.supsi.fscli.backend.business.mapper;

import java.io.File;

public interface IFsStateMapperBusiness {
    void toDTO();
    void fromDTO(String fileName);
    void toDTOas(File file);
}
