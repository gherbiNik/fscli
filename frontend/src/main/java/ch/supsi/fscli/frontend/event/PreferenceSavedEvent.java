package ch.supsi.fscli.frontend.event;

public class PreferenceSavedEvent extends AbstractEvent{
    public PreferenceSavedEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
}
