package custom;

import java.util.ArrayList;
import java.io.Serializable;

public class ContactList implements Serializable{
    private ArrayList<Contact> contacts;

    public ContactList() {
        contacts = new ArrayList<Contact>();
    }

     public ContactList(ArrayList<Contact> contacts) {
        this.contacts = contacts;
     }

     public void add(Contact contact) {
        contacts.add(contact);
     }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public Contact remove(int index) {
        return contacts.remove(index);
     }

     public boolean remove(Contact c) {
        return contacts.remove(c);
     }

     public Contact get(int index) {
        return contacts.get(index);
     }

     public boolean contains(Contact contact) {
        return contacts.contains(contact);
     }

     public String toString() {
        String result = "";
        for (Contact contact:contacts) {
            result += contact.toString() + "\n";
        }
        return result;
     }

     public String oneLines() {
        String result = "";
        for (Contact contact:contacts) {
            result += contact.oneLine() + "\n";
        }
        return result;
     }

     public String doubleSpace() {
         String result = "";
         for (Contact contact:contacts) {
             result += contact.toString() + "\n\n";
         }
         return result;
     }

     public int size() {
        return contacts.size();
     }

     public void set(int index, Contact contact){
        contacts.set(index, contact);
     }

     public boolean contains(String name, long phoneNumber){
        for (Contact contact: contacts) {
            if (contact.getName().equals(name) && contact.getPhoneNumber() == phoneNumber) {
                return true;
            }
        }
        return false;
     }
}
