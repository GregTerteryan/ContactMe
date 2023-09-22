package com.example.contactme.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.contactme.R;
import com.example.contactme.databinding.FragmentCalendarBinding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import custom.Event;
import custom.EventList;
import custom.MyApp;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    private final String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EventList events = new EventList();

    private CalendarView calendarView;

    private ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        load();
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        ArrayAdapter<String> adapter;
        ArrayList<Event> eventArrayList = events.getEvents();
        listview = view.findViewById(R.id.list_view);
        ArrayList<String> eventNames = new ArrayList<>();
        for (Event event: eventArrayList) {
            eventNames.add(event.getName());
        }
        adapter = new ArrayAdapter<>(MyApp.getAppContext(), android.R.layout.simple_list_item_single_choice, eventNames);
        listview.setAdapter(adapter);
        listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = eventArrayList.get(position);
                long notificationDate = selectedEvent.getNotificationDate();
                calendarView.setDate(notificationDate);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            Log.d("CalendarFragment", path);
        }
        catch (NullPointerException n) {
            Log.d("CalendarFragment", "NullPointerException");
            if (path == null) {
                Log.d("CalendarFragment", "path is null");
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
        } catch (IOException | ClassNotFoundException i) {
            events = new EventList();
            save();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        calendarView.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        calendarView.setVisibility(View.VISIBLE);
        listview.setVisibility(View.VISIBLE);
    }
}