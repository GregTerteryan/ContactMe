package com.example.contactme.ui.editEvent;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.contactme.R;
import com.example.contactme.ui.addEvent.AddEventViewModel;
import com.example.contactme.ui.events.EventsFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import custom.Contact;
import custom.ContactList;
import custom.Event;
import custom.EventList;
import custom.MyApp;
import custom.NotificationReceiver;

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
    private int selectedId;
    public static EditEventFragment newInstance() {
        return new EditEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        loadCL();
        load();
        selectedId = -1;
        listView = view.findViewById(R.id.edit_events_list);
        eventName = view.findViewById(R.id.editEventName);
        eventInfo = view.findViewById(R.id.editEventInfo);
        datePicker = view.findViewById(R.id.editDatePicker);
        timePicker = view.findViewById(R.id.editTimePicker);
        recyclerView = view.findViewById(R.id.editEventRecyclerView);
        back = view.findViewById(R.id.back_from_edit_event);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new EventsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });

        submit = view.findViewById(R.id.edit_event);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedId >= 0) {
                    String name = eventName.getText().toString();
                    String info = eventInfo.getText().toString();
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int dayOfMonth = datePicker.getDayOfMonth();
                    int hourOfDay = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    Event event = new Event(name, info, selected);
                    event.setNotificationDate(year, month, dayOfMonth, hourOfDay, minute);
                    if (events.size() == 0) {
                        event.setId(0);
                        Log.d("AddEventViewModel", event.getId() + "");
                    } else {
                        event.setId(events.get(events.size() - 1).getId() + 1);
                        Log.d("AddEventViewModel", event.getId() + "");
                    }
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyApp.getAppContext());
                    cancelNotification(notificationManager, events.get(selectedId));
                    scheduleNotification(event);
                    events.set(selectedId, event);
                    save();
                    Toast.makeText(MyApp.getAppContext(), "Event edited.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<String> adapter;
        ArrayList<Event> eventArrayList = events.getEvents();
        ArrayList<String> eventNames = new ArrayList<>();
        for (Event event: eventArrayList) {
            eventNames.add(event.getName());
        }
        adapter = new ArrayAdapter<>(MyApp.getAppContext(), android.R.layout.simple_list_item_single_choice, eventNames);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = eventArrayList.get(position);
                eventName.setText(selectedEvent.getName());
                eventInfo.setText(selectedEvent.getInfo());
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(selectedEvent.getNotificationDate());
                datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                timePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(cal.get(Calendar.MINUTE));
                for (int c = 0; c < contactList.size(); c++) {
                    contactList.get(c).setSelected(selectedEvent.getContacts().get(c).isSelected());
                }
                selectedId = position;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditEventViewModel.class);
        loadCL();
        selected = new ContactList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false); // Use a built-in layout for simplicity
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Contact contact = contactList.get(position);
                CheckedTextView contactNameTextView = (CheckedTextView) holder.itemView.findViewById(android.R.id.text1);
                contactNameTextView.setText(contact.getName());
                contactNameTextView.setChecked(contact.isSelected());
                contactNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recyclerView.isClickable()) {
                            if (contact.isSelected()) {
                                contact.setSelected(false);
                                selected.remove(contact);
                            } else {
                                contact.setSelected(true);
                                selected.add(contact);
                            }
                            contactNameTextView.setChecked(contact.isSelected());
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return contactList.size();
            }
        });
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

    public void saveCL() {
        try {
            if (path == null) {
                Log.d("ContactsViewModel", "context is null");
            }
            if (path != null) {
                Log.d("ContactsViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/contacts.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(contactList);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException i) {
        }
        catch (NullPointerException n) {
            Log.d("ContactsViewModel", "NullPointerException");
            if (path == null) {
                Log.d("ContactsViewModel", "path is null");
            }
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

    private void cancelNotification(NotificationManagerCompat notificationManager, Event event) {
        int notificationId = event.getId();
        notificationManager.cancel(notificationId);
    }


}