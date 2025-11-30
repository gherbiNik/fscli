package ch.supsi.fscli.frontend.event;

public class FirstFileSystemCreationEvent extends AbstractEvent{
    public FirstFileSystemCreationEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
