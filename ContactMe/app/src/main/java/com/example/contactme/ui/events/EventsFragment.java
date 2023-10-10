package com.example.contactme.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactme.MainActivity;
import com.example.contactme.R;
import com.example.contactme.databinding.FragmentEventsBinding;
import com.example.contactme.ui.addEvent.AddEventFragment;
import com.example.contactme.ui.editEvent.EditEventFragment;
import com.example.contactme.ui.removeEvent.RemoveEventFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.CollapsibleEventListAdapter;
import custom.EventList;
import custom.MyApp;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel eventsViewModel;
    private Button btnNext;
    private Button btnRem;
    private Button btnEdit;
    private RecyclerView collapsible;
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EventList events = new EventList();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        collapsible = root.findViewById(R.id.collapsible_events);

        btnNext = root.findViewById(R.id.add_event);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new AddEventFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        btnEdit = root.findViewById(R.id.go_edit_events);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new EditEventFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        btnRem = root.findViewById(R.id.removeanevent);
        btnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new RemoveEventFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load();
        collapsible.setLayoutManager(new LinearLayoutManager(getActivity()));
        collapsible.setClickable(true);
        collapsible.setAdapter(new CollapsibleEventListAdapter(events, true));
    }

    @Override
    public void onPause() {
        super.onPause();
        collapsible.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnRem.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        collapsible.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnRem.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.VISIBLE);
        load();
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

        } catch (IOException | ClassNotFoundException i) {
            events = new EventList();
            save();
        }
    }
}