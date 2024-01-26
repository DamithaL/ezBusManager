package ezbus.mit20550588.manager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.BusModel;

public class BusListAdapter extends ListAdapter<BusModel, BusListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<BusModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<BusModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull BusModel oldItem, @NonNull BusModel newItem) {
            return oldItem.getBusId() == newItem.getBusId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull BusModel oldItem, @NonNull BusModel newItem) {
            return oldItem.getBusNickName().equals(newItem.getBusNickName()) &&
                    oldItem.getBusRegNumber().equals(newItem.getBusRegNumber()) &&
                    oldItem.getBusRouteNumber().equals(newItem.getBusRouteNumber()) &&
                    oldItem.getBusRouteName().equals(newItem.getBusRouteName()) &&
                    oldItem.getBusEmergencyNumber().equals(newItem.getBusEmergencyNumber()) &&
                    oldItem.getBusColor().equals(newItem.getBusColor());
        }
    };

    // Add a listener member variable and a method to set the listener
    private OnItemClickListener listener;
    private RecyclerView recyclerView;

    public BusListAdapter() {
        super(DIFF_CALLBACK);
    }

    // onCreateViewHolder to inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_layout_bus_accounts, parent, false);
        return new ViewHolder(itemView);
    }

    // onBindViewHolder to bind the data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusModel currentBus = getItem(position);

        String routeNumberName = currentBus.getBusRouteNumber() + " | " + currentBus.getBusRouteName();
        String busNickName = currentBus.getBusNickName();
        String busRegNumber = "Reg. Number: " + currentBus.getBusRegNumber();
        String busEmergencyNumber = "Emergency: " + currentBus.getBusEmergencyNumber();

        holder.nickNameTextView.setText(busNickName);
        holder.busRegNumberTextView.setText(busRegNumber);
        holder.busRouteNumberNameTextView.setText(routeNumberName);
        holder.busEmergencyNumberTextView.setText(busEmergencyNumber);

        holder.busImageView.setColorFilter(Integer.parseInt(currentBus.getBusColor()));

    }

    // ViewHolder class to hold the views
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nickNameTextView;
        private TextView busRegNumberTextView;
        private TextView busRouteNumberNameTextView;
        private TextView busEmergencyNumberTextView;
        private ImageView busImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickNameTextView = itemView.findViewById(R.id.busNickNameTextView);
            busRegNumberTextView = itemView.findViewById(R.id.busNumberTextView);
            busRouteNumberNameTextView = itemView.findViewById(R.id.routeNameNumberTextView);
            busEmergencyNumberTextView = itemView.findViewById(R.id.emergencyNumberTextView);
            busImageView = itemView.findViewById(R.id.busImageView);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position));
                }
            });

        }
    }

    // interface for item click events
    public interface OnItemClickListener {
        void onItemClick(BusModel bus);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Method to set recent searches and scroll to the top
    public void scrollToTop() {
        if (recyclerView != null && getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(0);

        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}


