package custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactme.R;

public class CollapsibleEventListAdapter extends RecyclerView.Adapter<CollapsibleEventListAdapter.ViewHolder>{
    private EventList eventList;

    private boolean clickable;

    public CollapsibleEventListAdapter(EventList eventList, boolean clickable) {
        this.eventList = eventList;
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
    public CollapsibleEventListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scrollable_check, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollapsibleEventListAdapter.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        CheckedTextView itemNameTextView = (CheckedTextView) holder.checkedTextView;
        itemNameTextView.setText(event.getName());
        itemNameTextView.setChecked(event.isSelected());
        itemNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable) {
                    if (event.isSelected()) {
                        event.setSelected(false);
                        holder.checkedTextView.setChecked(event.isSelected());
                        itemNameTextView.setText(event.getName());
                    } else {
                        event.setSelected(true);
                        itemNameTextView.setText(event.toString());
                        holder.checkedTextView.setChecked(event.isSelected());
                    }
                    itemNameTextView.setChecked(event.isSelected());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView checkedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedTextView = itemView.findViewById(R.id.check_collapse);
        }
    }
}
