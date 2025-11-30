package ch.supsi.fscli.backend.application.mapper;

import ch.supsi.fscli.backend.business.dto.IFsStateMapper;

import java.io.File;

public class FsStateMapperApplication implements IFsStateMapperApplication{
    private static FsStateMapperApplication myself;
    private IFsStateMapper iFsStateMapper;

    private FsStateMapperApplication() {
    }

    public static FsStateMapperApplication getInstance(IFsStateMapper iFsStateMapper) {
        if (myself == null) {
            myself = new FsStateMapperApplication();
            myself.intialize(iFsStateMapper);
        }
        return myself;
    }

    private void intialize(IFsStateMapper iFsStateMapper) {
        this.iFsStateMapper = iFsStateMapper;
    }
    @Override
    public void toDTO() {
        iFsStateMapper.toDTO();
    }

    @Override
    public void fromDTO(String fileName) {
        iFsStateMapper.fromDTO(fileName);
    }

    @Override
    public void toDTOas(File file) {
        iFsStateMapper.toDTOas(file);
    }
}
