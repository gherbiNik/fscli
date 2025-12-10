package ch.supsi.fscli.frontend.event;

public class FileSystemSavedAs extends AbstractEvent{


    public FileSystemSavedAs(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
