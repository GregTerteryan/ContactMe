package custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactme.R;

public class CollapsibleContactListAdapter extends RecyclerView.Adapter<CollapsibleContactListAdapter.ViewHolder>{
    private ContactList contactList;

    private boolean clickable;

    public CollapsibleContactListAdapter(ContactList contactList, boolean clickable) {
        this.contactList = contactList;
        this.clickable = clickable;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @NonNull
    @Override
    public CollapsibleContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scrollable_check, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollapsibleContactListAdapter.ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        CheckedTextView itemNameTextView = (CheckedTextView) holder.checkedTextView;
        itemNameTextView.setText(contact.getName());
        itemNameTextView.setChecked(contact.isSelected());
        itemNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable) {
                    if (contact.isSelected()) {
                        contact.setSelected(false);
                        holder.checkedTextView.setChecked(contact.isSelected());
                        itemNameTextView.setText(contact.getName());
                    } else {
                        contact.setSelected(true);
                        itemNameTextView.setText(contact.toString());
                        holder.checkedTextView.setChecked(contact.isSelected());
                    }
                    itemNameTextView.setChecked(contact.isSelected());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView checkedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedTextView = itemView.findViewById(R.id.check_collapse);
        }
    }
}
