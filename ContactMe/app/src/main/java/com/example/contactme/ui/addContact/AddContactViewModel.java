package com.example.contactme.ui.addContact;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.Contact;
import custom.ContactList;
import custom.Event;
import custom.MyApp;
import custom.NotificationReceiver;

public class AddContactViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private ContactList contacts = new ContactList();

    public AddContactViewModel() {
        load();
    }
    public void save() {
        try {
            if (path == null) {
                Log.d("ContactsViewModel", "context is null");
            }
            if (path != null) {
                Log.d("AddContactViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/contacts.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(contacts);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException i) {
            Log.d("AddContactViewModel", "IOException");
        }
        catch (NullPointerException n) {
            Log.d("ContactsViewModel", "NullPointerException");
            if (path == null) {
                Log.d("ContactsViewModel", "path is null");
            }
        }
    }
    public void load() {
        contacts = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/contacts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            contacts = (ContactList) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            save();
        }
    }
    public void makeContact(String name, String info, long number, int days, int weeks) {
        Contact contact = new Contact(name, info, number, days, weeks);
        if (contacts.size() == 0) {
            contact.setId(1000);
        }
        else {
            contact.setId(contacts.get(contacts.size()-1).getId() + 1);
        }
        scheduleNotification(contact);
        contacts.add(contact);
        save();
    }
    public void scheduleNotification(Contact contact) {
        String contactName = contact.getMethodOfContact() + contact.getName();
        String contactInfo = "You haven't " + contact.getMethodOfContact() + "ed " + contact.getName() + " for " + contact.getContactWeeks() + " weeks and " + contact.getContactDays() + " days.";
        int contactId = contact.getId();
        long contactTimeMillis = contact.getMilliseconds();

        Context context = MyApp.getAppContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("eventName", contactName);
        notificationIntent.putExtra("eventInfo", contactInfo);
        notificationIntent.putExtra("eventID", contactId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, contactId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, contactTimeMillis, pendingIntent);
    }
}