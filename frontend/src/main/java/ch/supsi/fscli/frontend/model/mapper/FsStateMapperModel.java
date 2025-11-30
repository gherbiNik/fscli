package ch.supsi.fscli.frontend.model.mapper;

import ch.supsi.fscli.backend.application.mapper.IFsStateMapperApplication;

import java.io.File;

public class FsStateMapperModel implements IFsStateMapperModel {
    private static FsStateMapperModel instance;
    private IFsStateMapperApplication iFsStateMapperApplication;

    private FsStateMapperModel() {
    }

    public static FsStateMapperModel getInstance(IFsStateMapperApplication iFsStateMapperApplication) {
        if (instance == null) {
            instance = new FsStateMapperModel();
            instance.initialize(iFsStateMapperApplication);
        }
        return instance;
    }

    private void initialize(IFsStateMapperApplication iFsStateMapperApplication) {
        this.iFsStateMapperApplication = iFsStateMapperApplication;
    }

    @Override
    public void save() {
        iFsStateMapperApplication.toDTO();
    }

    @Override
    public void open(String fileName) {
        iFsStateMapperApplication.fromDTO(fileName);
    }

    @Override
    public void saveAs(File file) {
        iFsStateMapperApplication.toDTOas(file);
    }
}
