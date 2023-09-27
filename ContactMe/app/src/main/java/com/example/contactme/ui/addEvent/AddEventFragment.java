package com.example.contactme.ui.addEvent;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.contactme.R;
import com.example.contactme.ui.events.EventsFragment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import custom.Contact;
import custom.ContactList;
import custom.MyApp;

public class AddEventFragment extends Fragment {

    private AddEventViewModel mViewModel;
    private ContactList contactList;
    private ContactList selected = new ContactList();
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private RecyclerView recyclerView;
    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loadCL();
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        EditText name = view.findViewById(R.id.editTextTextEventName);
        EditText info = view.findViewById(R.id.editTextTextEventInfo);
        DatePicker datePicker = view.findViewById(R.id.datePicker);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        recyclerView = view.findViewById(R.id.eventRecyclerView);
        recyclerView.setClickable(true);
        Button btnBack = view.findViewById(R.id.back_to_events);
        btnBack.setOnClickListener(new View.OnClickListener() {
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
        Button submitButton = view.findViewById(R.id.make_event);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = name.getText().toString();
                String eventInfo = info.getText().toString();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                int hourOfDay = timePicker.getHour();
                int minute = timePicker.getMinute();
                AddEventViewModel viewModel = new ViewModelProvider(requireActivity()).get(AddEventViewModel.class);
                viewModel.makeEvent(eventName,eventInfo,selected,year,month,dayOfMonth,hourOfDay,minute);
                Toast.makeText(MyApp.getAppContext(), "Event added.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

}