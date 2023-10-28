package com.example.contactme.ui.editContact;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.contactme.R;
import com.example.contactme.ui.contacts.ContactsFragment;

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
import custom.MyApp;
import custom.NotificationReceiver;

public class EditContactFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button back;
    private Button submit;
    private EditText name;
    private EditText method;
    private EditText phoneNumber;
    private EditText weeks;
    private EditText days;
    @SuppressLint("StaticFieldLeak")
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private ContactList contacts = new ContactList();
    private int selectedId;
    private EditContactViewModel mViewModel;

    public static EditContactFragment newInstance() {
        return new EditContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_contact, container, false);
        load();
        selectedId = -1;
        recyclerView = view.findViewById(R.id.editList);
        back = view.findViewById(R.id.backFromEdit);
        submit = view.findViewById(R.id.edit_contact);
        name = view.findViewById(R.id.contactName);
        method = view.findViewById(R.id.contactMethod);
        phoneNumber = view.findViewById(R.id.contactPhone);
        weeks = view.findViewById(R.id.contactWeeks);
        days = view.findViewById(R.id.contactDays);

        ArrayAdapter<String> adapter;
        ArrayList<Contact> contactArrayList = contacts.getContacts();
        ArrayList<String> contactNames = new ArrayList<>();
        for (Contact contact: contactArrayList) {
            contactNames.add(contact.getName());
        }
        adapter = new ArrayAdapter<>(MyApp.getAppContext(), android.R.layout.simple_list_item_single_choice, contactNames);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new ContactsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText[] texts = {name, method, phoneNumber, days, weeks};
                boolean invalid = false;
                if ((Integer.parseInt(days.getText().toString()) < 0 || Integer.parseInt(weeks.getText().toString()) < 0) || (Integer.parseInt(days.getText().toString()) == 0 && Integer.parseInt(weeks.getText().toString()) < 1) || (Integer.parseInt(days.getText().toString()) < 1 & Integer.parseInt(weeks.getText().toString()) == 0)) {
                    invalid = true;
                    Toast.makeText(MyApp.getAppContext(), "Must have a positive notification frequency.", Toast.LENGTH_SHORT).show();
                }
                for (EditText text:texts) {
                    if (text.getText().toString().equals("")) {
                        invalid = true;
                        Toast.makeText(MyApp.getAppContext(), "Please enter something in every field.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (selectedId >= 0 && !invalid) {
                    Contact contact = contacts.get(selectedId);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyApp.getAppContext());
                    cancelNotification(notificationManager, contact);
                    contact.setName(name.getText().toString());
                    contact.setMethodOfContact(method.getText().toString());
                    contact.setPhoneNumber(phoneNumber.getText().toString());
                    contact.setContactWeeks(Integer.parseInt(weeks.getText().toString()));
                    contact.setContactDays(Integer.parseInt(days.getText().toString()));
                    scheduleNotification(contact);
                    Toast.makeText(MyApp.getAppContext(), "Contact edited.", Toast.LENGTH_SHORT).show();
                    save();
                }


            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditContactViewModel.class);

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
                            } else {
                                for (int c = 0; c < contacts.size(); c++) {
                                    if (c == holder.getAdapterPosition()) {
                                        contact.setSelected(true);
                                        selectedId = c;
                                        name.setText(contact.getName());
                                        method.setText(contact.getMethodOfContact());
                                        phoneNumber.setText(contact.getPhoneNumber());
                                        days.setText(String.valueOf(contact.getContactDays()));
                                        weeks.setText(String.valueOf(contact.getContactWeeks()));
                                        notifyItemChanged(c);
                                    }
                                    else {
                                        contacts.get(c).setSelected(false);
                                        notifyItemChanged(c);
                                    }
                                }

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
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onPause() {
        super.onPause();
        recyclerView.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        method.setVisibility(View.GONE);
        phoneNumber.setVisibility(View.GONE);
        weeks.setVisibility(View.GONE);
        days.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        method.setVisibility(View.VISIBLE);
        phoneNumber.setVisibility(View.VISIBLE);
        weeks.setVisibility(View.VISIBLE);
        days.setVisibility(View.VISIBLE);
    }

    public void scheduleNotification(Contact contact) {
        String contactName = contact.getMethodOfContact() + contact.getName();
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