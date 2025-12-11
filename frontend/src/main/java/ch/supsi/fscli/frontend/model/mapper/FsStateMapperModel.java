package ch.supsi.fscli.frontend.model.mapper;

import ch.supsi.fscli.backend.application.mapper.IFsStateMapperApplication;
import ch.supsi.fscli.frontend.event.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

@Singleton
public class FsStateMapperModel implements IFsStateMapperModel {
    private final IFsStateMapperApplication iFsStateMapperApplication;
    private final PropertyChangeSupport support;

    @Inject
    public FsStateMapperModel(IFsStateMapperApplication iFsStateMapperApplication) {
        this.iFsStateMapperApplication = iFsStateMapperApplication;
        this.support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    @Override
    public void save() {
        iFsStateMapperApplication.toDTO();
        support.firePropertyChange(new FileSystemSaved(this, "",null, iFsStateMapperApplication.getCurrentFileAbsolutePath()));
    }

    @Override
    public void open(String fileName) {
        iFsStateMapperApplication.fromDTO(fileName);
        support.firePropertyChange(new FileSystemOpenEvent(this, "",null, iFsStateMapperApplication.getCurrentFileAbsolutePath()));

    }

    @Override
    public void saveAs(File file) {
        iFsStateMapperApplication.toDTOas(file);
        support.firePropertyChange(new FileSystemSavedAs(this, "",null, iFsStateMapperApplication.getCurrentFileAbsolutePath()));

    }
}
