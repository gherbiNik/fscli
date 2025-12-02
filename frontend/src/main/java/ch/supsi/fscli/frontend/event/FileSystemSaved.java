package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;

public class FileSystemSaved extends AbstractEvent {
    public FileSystemSaved(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
