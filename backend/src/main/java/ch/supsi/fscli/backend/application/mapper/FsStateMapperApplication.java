package ch.supsi.fscli.backend.application.mapper;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.backend.business.dto.IFsStateMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;

@Singleton
public class FsStateMapperApplication implements IFsStateMapperApplication{

    private final IFsStateMapper iFsStateMapper;
    private final IFileSystemApplication fileSystemApplication;

    @Inject
    public FsStateMapperApplication(IFsStateMapper iFsStateMapper, IFileSystemApplication fileSystemApplication) {
        this.iFsStateMapper = iFsStateMapper;
        this.fileSystemApplication = fileSystemApplication;
    }

    @Override
    public void toDTO() {
        iFsStateMapper.toDTO();
    }

    @Override
    public void fromDTO(String fileName) {
        // Le dipendenze sono iniettate
        if (!fileSystemApplication.isFileSystemCreated())
            fileSystemApplication.createFileSystem();
        iFsStateMapper.fromDTO(fileName);
    }

    @Override
    public void toDTOas(File file) {
        iFsStateMapper.toDTOas(file);
    }

    @Override
    public String getCurrentFileAbsolutePath() {
        return iFsStateMapper.getCurrentFileAbsolutePath();
    }
}