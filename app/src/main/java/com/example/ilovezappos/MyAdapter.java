package com.example.ilovezappos;

import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<TableRow> mDataset;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView bids,asks;
        public MyViewHolder(View view) {
            super(view);
            bids=(TextView)view.findViewById(R.id.bids);
            asks=(TextView)view.findViewById(R.id.asks);
        }
    }

    public MyAdapter(List<TableRow> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_row, parent, false);
        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TableRow row=mDataset.get(position);
        holder.bids.setText(row.getBids());
        holder.asks.setText(row.getAsks());
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
