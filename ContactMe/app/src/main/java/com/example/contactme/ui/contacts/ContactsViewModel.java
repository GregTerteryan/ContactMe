package com.example.contactme.ui.contacts;

import android.annotation.SuppressLint;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.*;

public class ContactsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    @SuppressLint("StaticFieldLeak")
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private ContactList contacts = new ContactList();

    public ContactsViewModel() {
        mText = new MutableLiveData<>();
        load();
        mText.setValue(contacts.toString());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void save() {
        try {
            if (path == null) {
                Log.d("ContactsViewModel", "context is null");
            }
            if (path != null) {
                Log.d("ContactsViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/contacts.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(contacts);
                out.close();
                fileOut.close();
                mText.setValue("Data saved.");
            }
        }
        catch (IOException i) {
            mText.setValue(path);
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
            Log.d("ContactsViewModel","Successfully loaded.");
            mText.setValue(contacts.doubleSpace());
            if (contacts == null) {
                Log.d("ContactsViewModel", "contacts is null");
            }
        } catch (IOException | ClassNotFoundException i) {
            contacts = new ContactList();
            save();
        }
    }

    public void setText(String str) {
        mText.setValue(str);
    }
}