package com.example.contactme.ui.removeContact;

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

import com.example.contactme.R;
import com.example.contactme.ui.contacts.ContactsFragment;
import com.example.contactme.ui.events.EventsFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.Contact;
import custom.ContactList;
import custom.MyApp;

public class RemoveContactFragment extends Fragment {

    private RemoveContactViewModel mViewModel;

    private ContactList contactList;
    private ContactList selected = new ContactList();
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private RecyclerView recyclerView;

    public static RemoveContactFragment newInstance() {
        return new RemoveContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_contact, container, false);

        recyclerView = view.findViewById(R.id.remContRecyclerView);
        recyclerView.setClickable(true);
        Button submit = view.findViewById(R.id.remove_contact);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int c = 0; c < selected.size(); c++) {
                    contactList.remove(selected.get(c));
                }
                save();
                load();
            }
        });
        Button back = view.findViewById(R.id.back_to_contacts);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on button click, e.g. navigate to next fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new ContactsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RemoveContactViewModel.class);
        load();
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

    public void save() {
        try {
            if (path == null) {
                Log.d("RemoveContactViewModel", "context is null");
            }
            if (path != null) {
                Log.d("RemoveContactViewModel", path);
                FileOutputStream fileOut = new FileOutputStream(path + "/contacts.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(contactList);
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
        contactList = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + "/contacts.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            contactList = (ContactList) in.readObject();
            in.close();
            fileIn.close();
            Log.d("RemoveContactViewModel","Successfully loaded");
        } catch (IOException | ClassNotFoundException i) {
            contactList = new ContactList();
            Log.d("RemoveContactViewModel","wtf happened");
            save();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}