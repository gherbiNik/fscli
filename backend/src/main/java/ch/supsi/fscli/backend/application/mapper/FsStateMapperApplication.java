package ch.supsi.fscli.backend.application.mapper;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.backend.business.dto.IFsStateMapper;

import java.io.File;

public class FsStateMapperApplication implements IFsStateMapperApplication{
    private static FsStateMapperApplication myself;
    private IFsStateMapper iFsStateMapper;
    private IFileSystemApplication fileSystemApplication;

    private FsStateMapperApplication() {
    }

    public static FsStateMapperApplication getInstance(IFsStateMapper iFsStateMapper, IFileSystemApplication fileSystemApplication) {
        if (myself == null) {
            myself = new FsStateMapperApplication();
            myself.intialize(iFsStateMapper, fileSystemApplication);
        }
        return myself;
    }

    private void intialize(IFsStateMapper iFsStateMapper,  IFileSystemApplication fileSystemApplication) {
        this.iFsStateMapper = iFsStateMapper;
        this.fileSystemApplication = fileSystemApplication;
    }
    @Override
    public void toDTO() {
        iFsStateMapper.toDTO();
    }

    @Override
    public void fromDTO(String fileName) {
        if (!fileSystemApplication.isFileSystemCreated())
            fileSystemApplication.createFileSystem();
        iFsStateMapper.fromDTO(fileName);
    }

    @Override
    public void toDTOas(File file) {
        iFsStateMapper.toDTOas(file);
    }
}
