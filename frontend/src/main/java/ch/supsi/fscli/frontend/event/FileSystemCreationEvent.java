package ch.supsi.fscli.frontend.event;

public class FileSystemCreationEvent extends AbstractEvent{
    public FileSystemCreationEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
