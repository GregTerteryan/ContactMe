package com.example.contactme.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.contactme.R;
import com.example.contactme.databinding.FragmentContactsBinding;
import com.example.contactme.ui.addContact.AddContactFragment;
import com.example.contactme.ui.removeContact.RemoveContactFragment;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private ContactsViewModel contactsViewModel;
    private TextView textView;
    private Button btnNext;
    private Button btnRem;

    /*
    REVAMP
    editContactsFragment
     */

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textContacts;
        contactsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
    public void onPause() {
        super.onPause();
        contactsViewModel.setText("");
        textView.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnRem.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnRem.setVisibility(View.VISIBLE);
        contactsViewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}