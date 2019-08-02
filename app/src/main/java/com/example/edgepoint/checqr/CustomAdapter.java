package com.example.edgepoint.checqr;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private List<AttendeesModel> attendeesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, time;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
        }
    }


    public CustomAdapter(List<AttendeesModel> attendeesList) {
        this.attendeesList = attendeesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lv_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AttendeesModel attendees = attendeesList.get(position);
        holder.name.setText(attendees.getName());
        holder.time.setText(attendees.getTime());
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
    }

}