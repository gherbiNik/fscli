package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.frontend.event.ExitAbortedEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Singleton
public class ExitModel implements IExitModel{
    private final IFileSystemApplication application;
    private final PropertyChangeSupport support;

    @Inject
    public ExitModel(IFileSystemApplication application) {
        this.application = application;
        this.support = new PropertyChangeSupport(this);;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }


    @Override
    public boolean isExitPossible() {
        // ESCO se -> non ci sono dati da salvare o filesystem non creato
        System.out.println("DEBUG: "+ application.isFileSystemCreated()+" "+ application.isDataToSave());
        boolean isExitPossible = !application.isDataToSave() || !application.isFileSystemCreated();
        if (!isExitPossible)
            support.firePropertyChange(new ExitAbortedEvent(this));

        return isExitPossible;
    }
}
