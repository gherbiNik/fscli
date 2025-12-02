package ch.supsi.fscli.frontend.model.mapper;

import ch.supsi.fscli.backend.application.mapper.IFsStateMapperApplication;
import ch.supsi.fscli.frontend.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

public class FsStateMapperModel implements IFsStateMapperModel {
    private static FsStateMapperModel instance;
    private IFsStateMapperApplication iFsStateMapperApplication;
    private final PropertyChangeSupport support;


    private FsStateMapperModel() {
        this.support = new PropertyChangeSupport(this);
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

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }


    @Override
    public void save() {
        iFsStateMapperApplication.toDTO();
        support.firePropertyChange(new FileSystemSaved(this,"filesystem saved",null,null));
    }

    @Override
    public void open(String fileName) {
        iFsStateMapperApplication.fromDTO(fileName);
        support.firePropertyChange(new FileSystemOpenEvent(this,"filesystem open from file",null,null));

    }

    @Override
    public void saveAs(File file) {
        iFsStateMapperApplication.toDTOas(file);
        support.firePropertyChange(new FileSystemSavedAs(this,"filesystem saved as",null,null));

    }
}
