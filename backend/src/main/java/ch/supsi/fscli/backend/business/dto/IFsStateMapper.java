package ch.supsi.fscli.backend.business.dto;


import java.io.File;

public interface IFsStateMapper {
    void toDTO();
    void fromDTO(String fileName);
    void toDTOas(File file);
}

