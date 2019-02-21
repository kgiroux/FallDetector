package com.aimove.iot.falldetector.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aimove.iot.falldetector.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryItemViewHolder extends RecyclerView.ViewHolder {

    /**
     * TextView on the view holder
     */
    private TextView textView;

    public HistoryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textViewFallHistory);

    }

    public TextView getTextView() {
        return textView;
    }
}
