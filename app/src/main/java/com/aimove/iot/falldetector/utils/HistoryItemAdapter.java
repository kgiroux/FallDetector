package com.aimove.iot.falldetector.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.aimove.iot.falldetector.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that will display the list of falling
 * @author Kevin Giroux
 */
public class HistoryItemAdapter extends RecyclerView.Adapter {

    /**
     * List of Fall
     */
    private List<String> historyFall = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_adapter,viewGroup ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof HistoryItemViewHolder){
            HistoryItemViewHolder historyItemViewHolder = (HistoryItemViewHolder) viewHolder;
            historyItemViewHolder.getTextView().setText(this.historyFall.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return historyFall.size();
    }

    /**
     * Add a date of falling in the history
     * @param dateOfFalling date of fall
     */
    public void addFall(final String dateOfFalling){
        this.historyFall.add(dateOfFalling);
    }
}
