package ch.supsi.fscli.frontend.event;

public class ClearEvent extends AbstractEvent {
    public ClearEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
