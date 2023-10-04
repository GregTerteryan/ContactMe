package com.example.contactme.ui.addContact;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactme.R;
import com.example.contactme.ui.contacts.ContactsFragment;

import custom.MyApp;

public class AddContactFragment extends Fragment {

    private AddContactViewModel mViewModel;

    public static AddContactFragment newInstance() {
        return new AddContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        EditText name = view.findViewById(R.id.editTextTextPersonName);
        EditText method = view.findViewById(R.id.editTextTextPersonMethod);
        EditText number = view.findViewById(R.id.editTextPhone);
        EditText days = view.findViewById(R.id.editTextDays);
        EditText weeks = view.findViewById(R.id.editTextWeeks);
        Button submitButton = view.findViewById(R.id.make_contact);
        Button btnBack = view.findViewById(R.id.back_to_contacts);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText[] texts = {name, method, number, days, weeks};
                boolean invalid = false;
                for (EditText text:texts) {
                    if (text.getText().toString().equals("")) {
                        invalid = true;
                    }
                }
                if (!invalid) {
                    String contactName = name.getText().toString();
                    String contactMethod = method.getText().toString();
                    long contactNumber = Long.parseLong(number.getText().toString());
                    int contactDays = Integer.parseInt(days.getText().toString());
                    int contactWeeks = Integer.parseInt(weeks.getText().toString());

                    AddContactViewModel viewModel = new ViewModelProvider(requireActivity()).get(AddContactViewModel.class);

                    viewModel.makeContact(contactName, contactMethod, contactNumber, contactWeeks, contactDays);
                    Toast.makeText(MyApp.getAppContext(), "Contact added.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MyApp.getAppContext(), "Please fill all spaces.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
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
        mViewModel = new ViewModelProvider(this).get(AddContactViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}