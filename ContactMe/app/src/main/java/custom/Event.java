package custom;

import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

public class Event implements Serializable{

    /*
    REVAMP:
    editEventsFragment
    -deletes and resends notification
     */
    private ContactList contacts;
    private String name;
    private String info;

    private int id;

    private boolean isSelected;
    private long notificationDate;
    public Event() {
        contacts = new ContactList();
        name = "";
        info = "";
        isSelected = false;
    }

    public Event(String name, String info, ContactList contacts) {
        this.name = name;
        this.info = info;
        this.contacts = contacts;
        notificationDate = 0;
        isSelected = false;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public ContactList getContacts() {
        return contacts;
    }

    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(notificationDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String dateString = dateFormat.format(calendar.getTime());


        return name + "\n" + info + "\n" + dateString + "\n" + contacts.toString();
    }

    public void setNotificationDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.set(year, month, day, hour, minute, 0);
        notificationDate = cal.getTimeInMillis();
    }

    public long getNotificationDate() {
        return notificationDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
