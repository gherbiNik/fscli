package ch.supsi.fscli.frontend.event;

public class OutputEvent extends AbstractEvent {
    public OutputEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
