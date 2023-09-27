package com.example.contactme.ui.removeEvent;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.example.contactme.R;
import com.example.contactme.ui.events.EventsFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.Event;
import custom.EventList;
import custom.MyApp;

public class RemoveEventFragment extends Fragment {

    private RemoveEventViewModel mViewModel;

    private EventList eventList;
    private EventList selected = new EventList();
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private RecyclerView recyclerView;

    public static RemoveEventFragment newInstance() {
        return new RemoveEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_event, container, false);

        recyclerView = view.findViewById(R.id.remEvRecyclerView);
        recyclerView.setClickable(true);
        Button submit = view.findViewById(R.id.remove_event);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyApp.getAppContext());
                for (int c = 0; c < selected.size(); c++) {
                    cancelNotification(notificationManager, selected.get(c));
                    eventList.remove(selected.get(c));
                }
                save();
                load();
                Toast.makeText(MyApp.getAppContext(), "Event removed.", Toast.LENGTH_SHORT).show();
            }
        });
        Button back = view.findViewById(R.id.back_to_events);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on button click, e.g. navigate to next fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new EventsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void cancelNotification(NotificationManagerCompat notificationManager, Event event) {
        int notificationId = event.getId();
        notificationManager.cancel(notificationId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RemoveEventViewModel.class);
        load();
        selected = new EventList();
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
                Event event = eventList.get(position);
                CheckedTextView contactNameTextView = (CheckedTextView) holder.itemView.findViewById(android.R.id.text1);
                contactNameTextView.setText(event.getName());
                contactNameTextView.setChecked(event.isSelected());
                contactNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recyclerView.isClickable()) {
                            if (event.isSelected()) {
                                event.setSelected(false);
                                selected.remove(event);
                            } else {
                                event.setSelected(true);
                                selected.add(event);
                            }
                            contactNameTextView.setChecked(event.isSelected());
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return eventList.size();
            }
        });
    }

    public void save() {
        try {
            if (path == null) {
                Log.d("RemoveContactViewModel", "context is null");
            }
            if (path != null) {
                Log.d("RemoveContactViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/events.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(eventList);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException i) {
            Log.d("RemoveContactViewModel", "IOException");
        }
        catch (NullPointerException i) {
            Log.d("RemoveContactViewModel", "NullPointerException");
        }
    }
    public void load() {
        eventList = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/events.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            eventList = (EventList) in.readObject();
            in.close();
            fileIn.close();
            Log.d("RemoveContactViewModel","Successfully loaded");
        } catch (IOException | ClassNotFoundException i) {
            eventList = new EventList();
            Log.d("RemoveContactViewModel","wtf happened");
            save();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}