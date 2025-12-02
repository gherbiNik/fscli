package ch.supsi.fscli.frontend.model.mapper;

import java.beans.PropertyChangeListener;
import java.io.File;

public interface IFsStateMapperModel {
    void save();
    void open(String fileName);
    void saveAs(File file);
    void addPropertyChangeListener(PropertyChangeListener pcl);

}
