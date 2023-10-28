package com.example.contactme.ui.landingPage;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.example.contactme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Objects;

import custom.Contact;
import custom.ContactList;
import custom.MyApp;
import custom.NotificationReceiver;

public class LandingPageFragment extends Fragment {

    private LandingPageViewModel mViewModel;
    private Button submit;
    private RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private ContactList contacts;
    private ContactList selected;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    BottomNavigationView bottomNavigationView;

    public static LandingPageFragment newInstance() {
        return new LandingPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing_page, container, false);
        load();
        for (int c = 0; c < contacts.size(); c++) {
            contacts.get(c).setSelected(false);
        }
        save();

        recyclerView = view.findViewById(R.id.contacts_selection);
        submit = view.findViewById(R.id.exit_landing);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyApp.getAppContext());
                for (int c = 0; c < selected.size(); c++) {
                    cancelNotification(notificationManager, selected.get(c));
                }
                for (int c = 0; c < selected.size(); c++) {
                    scheduleNotification(selected.get(c));
                }
                Toast.makeText(MyApp.getAppContext(), "Great! Refreshed notifications!", Toast.LENGTH_SHORT).show();
                Cursor cursor = MyApp.getAppContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                if (cursor != null) {
                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    while (cursor.moveToNext()) {
                        String name = cursor.getString(nameColumnIndex);
                        String phoneNumber = cursor.getString(phoneNumberColumnIndex);
                        String number = "";
                        for (char ch: phoneNumber.toCharArray()) {
                            if (Character.isDigit(ch)) {
                                number += ch;
                            }
                        }
                        if (!contacts.contains(name, number)) {
                            Contact contact = new Contact(name, number);
                            if (contacts.size() == 0) {
                                contact.setId(1000000);
                            }
                            else {
                                contact.setId(contacts.get(contacts.size()-1).getId() + 1);
                            }
                            contacts.add(contact);
                            for (int c = 0; c < contacts.size(); c++) {
                                contacts.get(c).setSelected(false);
                            }
                            save();
                        }
                        Log.d("LandingPageFragment", name + " " + phoneNumber);
                    }
                    cursor.close();
                }
                bottomNavigationView.setVisibility(View.VISIBLE);
                NavHostFragment.findNavController(LandingPageFragment.this)
                        .navigate(R.id.navigation_contacts);

            }
        });
        while (ContextCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LandingPageViewModel.class);
        selected = new ContactList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setClickable(true);
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
                Contact contact = contacts.get(position);
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
                return contacts.size();
            }
        });
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void scheduleNotification(Contact contact) {
        String contactName = contact.getMethodOfContact() + " " + contact.getName();
        String contactInfo = "You haven't " + contact.getMethodOfContact() + "ed " + contact.getName() + " for " + contact.getContactWeeks() + " weeks and " + contact.getContactDays() + " days.";
        int contactId = contact.getId();
        long contactTimeMillis = Calendar.getInstance().getTimeInMillis() + contact.getMilliseconds();

        Context context = MyApp.getAppContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("eventName", contactName);
        notificationIntent.putExtra("eventInfo", contactInfo);
        notificationIntent.putExtra("eventId", contactId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, contactId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, contactTimeMillis, pendingIntent);
    }

    private void cancelNotification(NotificationManagerCompat notificationManager, Contact contact) {
        int notificationId = contact.getId();
        notificationManager.cancel(notificationId);
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
    public void load() {
        contacts = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/contacts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            contacts = (ContactList) in.readObject();
            in.close();
            fileIn.close();
            Log.d("ContactsViewModel","Successfully loaded.");
            if (contacts == null) {
                Log.d("ContactsViewModel", "contacts is null");
            }
        } catch (IOException | ClassNotFoundException i) {
            contacts = new ContactList();
            save();
        }
    }

}