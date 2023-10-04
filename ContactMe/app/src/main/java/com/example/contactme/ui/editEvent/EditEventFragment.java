package com.example.contactme.ui.editEvent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.contactme.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.ContactList;
import custom.EventList;
import custom.MyApp;

public class EditEventFragment extends Fragment {

    private EditEventViewModel mViewModel;

    private ListView listView;

    private EventList events = new EventList();
    private ContactList contactList;
    private ContactList selected = new ContactList();
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EditText eventName;
    private EditText eventInfo;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private RecyclerView recyclerView;

    private Button back;
    private Button submit;

    public static EditEventFragment newInstance() {
        return new EditEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        loadCL();
        load();

        listView = view.findViewById(R.id.edit_events_list);
        eventName = view.findViewById(R.id.editEventName);
        eventInfo = view.findViewById(R.id.editEventInfo);
        datePicker = view.findViewById(R.id.editDatePicker);
        timePicker = view.findViewById(R.id.editTimePicker);
        recyclerView = view.findViewById(R.id.editEventRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditEventViewModel.class);
        // TODO: Use the ViewModel
    }

    public void loadCL() {
        contactList = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/contacts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            contactList = (ContactList) in.readObject();
            in.close();
            fileIn.close();
            Log.d("AddEventFragment","Successfully loaded");
        } catch (IOException | ClassNotFoundException i) {
            contactList = new ContactList();
            Log.d("AddEventFragment","wtf happened");
        }
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

}