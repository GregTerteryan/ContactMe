package com.example.contactme.ui.events;

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

import com.example.contactme.MainActivity;
import com.example.contactme.R;
import com.example.contactme.databinding.FragmentEventsBinding;
import com.example.contactme.ui.addEvent.AddEventFragment;
import com.example.contactme.ui.removeEvent.RemoveEventFragment;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel eventsViewModel;
    private Button btnNext;
    private Button btnRem;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textEvents;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
    public void onPause() {
        super.onPause();
        eventsViewModel.setText("");
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
        eventsViewModel.load();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}