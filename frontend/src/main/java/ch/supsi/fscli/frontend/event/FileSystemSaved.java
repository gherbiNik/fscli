package ch.supsi.fscli.frontend.event;

public class FileSystemSaved extends AbstractEvent {


    public FileSystemSaved(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
