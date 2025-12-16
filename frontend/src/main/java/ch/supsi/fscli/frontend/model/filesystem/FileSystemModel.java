package ch.supsi.fscli.frontend.model.filesystem;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.FileSystemToSaved;
import ch.supsi.fscli.frontend.event.OutputEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Singleton
public class FileSystemModel implements IFileSystemModel {

    private final IFileSystemApplication application;
    private final PropertyChangeSupport support;

    @Inject
    public FileSystemModel(IFileSystemApplication application) {
        this.application = application;
        this.support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }


    @Override
    public void createFileSystem() {
        application.createFileSystem();
        support.firePropertyChange(new FileSystemCreationEvent(this));

    }

    @Override
    public String sendCommand(String userInput) {
        String result = application.sendCommand(userInput);

        // Magia per capire se è errore o no
        if (!result.contains("ERROR-")){
            if (application.isDataToSave()) {
                support.firePropertyChange(new FileSystemToSaved(this));
            }
        } else
            result = result.replace("ERROR-","");
        // Costruiamo il messaggio formattato
        String formattedOutput = "<user> " + userInput + "\n" + result + "\n";

        // Il model non sa chi c'è dall'altra parte. Se c'è la GUI, bene. Se non c'è, amen.
        if (result.equals("Perform Clear")) {
            support.firePropertyChange(new ClearEvent(this));
        } else {
            support.firePropertyChange(new OutputEvent(this, "OutputEvent", null, formattedOutput));
        }

        return result;
    }

    @Override
    public boolean isDataToSave() {
        return application.isDataToSave();
    }

    @Override
    public void setDataToSave(boolean dataToSave) {
        application.setDataToSave(dataToSave);
    }
}
