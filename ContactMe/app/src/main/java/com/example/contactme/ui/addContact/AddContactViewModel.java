package com.example.contactme.ui.addContact;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.Contact;
import custom.ContactList;
import custom.MyApp;

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
        contacts.add(new Contact(name, info, number, days, weeks));
        save();
    }
}