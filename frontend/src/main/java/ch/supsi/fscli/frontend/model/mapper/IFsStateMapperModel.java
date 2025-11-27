package ch.supsi.fscli.frontend.model.mapper;

import java.io.File;

public interface IFsStateMapperModel {
    void save();
    void open(String fileName);
    void saveAs(File file);
}
