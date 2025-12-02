package ch.supsi.fscli.frontend.event;

import java.beans.PropertyChangeEvent;

abstract public class AbstractEvent extends PropertyChangeEvent {

    public AbstractEvent(Object source) {
        this(source, null, null, null);
    }

    public AbstractEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }

}
