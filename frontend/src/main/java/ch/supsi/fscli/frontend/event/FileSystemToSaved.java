package ch.supsi.fscli.frontend.event;

public class FileSystemToSaved extends AbstractEvent{
    public FileSystemToSaved(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
