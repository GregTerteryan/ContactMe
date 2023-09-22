package com.example.contactme.ui.addEvent;

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

import custom.*;

public class AddEventViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private final String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EventList events = new EventList();

    public AddEventViewModel() {
        load();
    }

    public void save() {
        try {
            if (path == null) {
                Log.d("AddEventViewModel", "context is null");
            }
            if (path != null) {
                Log.d("AddEventViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/events.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(events);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException i) {
            Log.d("AddEventViewModel", "IOException");
        }
        catch (NullPointerException i) {
            Log.d("AddEventViewModel", "NullPointerException");
        }
    }
    public void load() {
        events = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/events.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            events = (EventList) in.readObject();
            in.close();
            fileIn.close();
            Log.d("AddEventViewModel","Successfully loaded");
        } catch (IOException | ClassNotFoundException i) {
            events = new EventList();
            Log.d("AddEventViewModel","wtf happened");
            save();
        }
    }

    public void makeEvent(String name, String info, ContactList cL,int year, int month, int day, int hour, int minute) {
        Event event = new Event(name, info, cL);
        event.setNotificationDate(year,month,day,hour,minute);
        if (events.size() == 0) {
            event.setId(0);
            Log.d("AddEventViewModel", event.getId() + "");
        }
        else {
            event.setId(events.get(events.size()-1).getId() + 1);
            Log.d("AddEventViewModel", event.getId() + "");
        }
        scheduleNotification(event);
        events.add(event);
        save();
    }

    public void scheduleNotification(Event event) {
        String eventName = event.getName();
        String eventInfo = event.getInfo();
        int eventId = event.getId();
        long eventTimeMillis = event.getNotificationDate();

        Context context = MyApp.getAppContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("eventName", eventName);
        notificationIntent.putExtra("eventInfo", eventInfo);
        notificationIntent.putExtra("eventID", eventId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, eventId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventTimeMillis, pendingIntent);
    }
}