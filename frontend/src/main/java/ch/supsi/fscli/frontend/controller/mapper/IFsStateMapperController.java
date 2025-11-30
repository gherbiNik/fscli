package ch.supsi.fscli.frontend.controller.mapper;

import java.io.File;

public interface IFsStateMapper {
    void save();
    void open(String fileName);
    void saveAs(File file);
}
