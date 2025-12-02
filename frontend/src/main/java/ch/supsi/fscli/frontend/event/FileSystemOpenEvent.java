package ch.supsi.fscli.frontend.event;

public class FileSystemOpenEvent extends AbstractEvent{
    public FileSystemOpenEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
