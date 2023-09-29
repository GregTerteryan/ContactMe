package com.example.contactme.ui.contacts;

import android.annotation.SuppressLint;
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

import com.example.contactme.R;
import com.example.contactme.databinding.FragmentContactsBinding;
import com.example.contactme.ui.addContact.AddContactFragment;
import com.example.contactme.ui.editContact.EditContactFragment;
import com.example.contactme.ui.removeContact.RemoveContactFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import custom.CollapsibleContactListAdapter;
import custom.ContactList;
import custom.MyApp;

public class ContactsFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();
    private ContactList contacts = new ContactList();
    private RecyclerView collapsible;
    private FragmentContactsBinding binding;
    private ContactsViewModel contactsViewModel;
    private Button btnNext;
    private Button btnEdit;
    private Button btnRem;

    /*
    REVAMP
    editContactsFragment

    collapsible contacts
     */

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        collapsible = root.findViewById(R.id.collapsible_contacts);

        btnNext = root.findViewById(R.id.add_contact);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new AddContactFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        btnEdit = root.findViewById(R.id.edit_contact);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new EditContactFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        btnRem = root.findViewById((R.id.rem_contact));
        btnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.myConstraintLayout, new RemoveContactFragment());
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
        collapsible.setAdapter(new CollapsibleContactListAdapter(contacts, true));
    }

    @Override
    public void onPause() {
        super.onPause();
        collapsible.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnRem.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        collapsible.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnRem.setVisibility(View.VISIBLE);
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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