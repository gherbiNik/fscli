package ch.supsi.fscli.backend.application.mapper;

import java.io.File;

public interface IFsStateMapperApplication {
    void toDTO();
    void fromDTO(String fileName);
    void toDTOas(File file);
}
