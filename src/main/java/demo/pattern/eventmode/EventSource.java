package demo.pattern.eventmode;

import java.util.ArrayList;
import java.util.List;

public class EventSource {
    private List<EventListener> eventListeners = new ArrayList<>();
    public void register(EventListener listener){
        eventListeners.add(listener);
    }
    public void publish(Event event){
        for (EventListener eventListener : eventListeners) {
            eventListener.processEvent(event);
        }
    }
}
