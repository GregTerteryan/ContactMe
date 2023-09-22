package com.example.contactme.ui.events;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

import custom.*;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EventList events = new EventList();

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        load();
        mText.setValue(events.toString());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String text) {
        mText.setValue(text);
    }
    public void save() {
        try {
            if (path != null) {
                FileOutputStream fileOut = new FileOutputStream(path + "/events.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(events);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException i) {
            mText.setValue(path);
        }
        catch (NullPointerException n) {
            Log.d("EventsViewModel", "NullPointerException");
            if (path == null) {
                Log.d("EventsViewModel", "path is null");
            }
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
            mText.setValue(events.toString());
        } catch (IOException | ClassNotFoundException i) {
            events = new EventList();
            save();
        }
    }
}