package custom;

import java.io.Serializable;
import java.util.Objects;

public class Contact implements Serializable{

    private String name;
    private String methodOfContact;
    private long phoneNumber;
    private int contactDays;
    private int contactWeeks;

    private int id;

    private boolean isSelected;

    public Contact() {
        name = "";
        methodOfContact = "";
        phoneNumber = -1;
        id = -1000000;
        contactDays = 0;
        contactWeeks = -1;
        isSelected = false;
    }

    public Contact(String name, long phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        methodOfContact = "contact";
        contactDays = 0;
        contactWeeks = -1;
        id = -1000000;
        isSelected = false;
    }

    public Contact(String name, String methodOfContact, long phoneNumber, int contactWeeks, int contactDays) {
        this.name = name;
        this.methodOfContact = methodOfContact;
        this.phoneNumber = phoneNumber;
        this.contactWeeks = contactWeeks;
        this.contactDays = contactDays;
        id = -1000000;
        isSelected = false;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getMethodOfContact() {
        return methodOfContact;
    }

    public String getName() {
        return name;
    }

    public void setMethodOfContact(String methodOfContact) {
        this.methodOfContact = methodOfContact;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return getPhoneNumber() == contact.getPhoneNumber() && Objects.equals(getName(), contact.getName()) && id == contact.getId();
    }

    public String toString() {
        return name + "\n" + methodOfContact + "\n" + contactWeeks + " weeks " + contactDays + " days" + "\n" + phoneNumber;
    }

    public String oneLine() {
        return name + " " + methodOfContact + " " + contactWeeks + " weeks " + contactDays + " days " + phoneNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(int id){this.id = id;}

    public int getId(){return id;}

    public int getContactDays() {
        return contactDays;
    }

    public void setContactDays(int contactDays) {
        this.contactDays = contactDays;
    }

    public int getContactWeeks() {
        return contactWeeks;
    }

    public void setContactWeeks(int contactWeeks) {
        this.contactWeeks = contactWeeks;
    }

    public int getTotalDays() {
        return contactWeeks * 7 + contactDays;
    }

    public long getMilliseconds() {
        return getTotalDays() * 86400000L;
    }
}
