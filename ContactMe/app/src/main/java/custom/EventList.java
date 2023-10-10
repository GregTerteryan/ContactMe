package custom;

import java.io.Serializable;
import java.util.ArrayList;

public class EventList implements Serializable {
    private ArrayList<Event> events;

    public EventList() {
        events = new ArrayList<Event>();
    }

    public EventList(ArrayList<Event> events) {
        this.events = events;
    }

    public void add(Event event) {
        events.add(event);
    }

    public Event remove(int index) {
        return events.remove(index);
    }

    public boolean remove(Event e) {
        return events.remove(e);
    }

    public Event get(int index) {
        return events.get(index);
    }

    public String toString() {
        String result = "";
        for (Event event:events) {
            result += event.toString() + "\n";
        }
        return result;
    }

    public void set(int index, Event event) {
        events.set(index, event);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public int size() {
        return events.size();
    }
}
