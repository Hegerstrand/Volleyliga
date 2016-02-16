package com.sportsapp.volleyliga.views.controllers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.PlayerStatisticModelWrapper;

public class PlayerStatisticHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTitle;
    public TextView tvValue;


    public PlayerStatisticHeaderViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvValue = (TextView) itemView.findViewById(R.id.tvValue);
    }

    public void setEntry(PlayerStatisticModelWrapper entry) {
        int textColor = entry.isNegativeStat ? Color.parseColor("#B71C1C") : Color.BLACK;
        tvTitle.setTextColor(textColor);
        tvTitle.setText(entry.title);
        tvValue.setTextColor(textColor);
        tvValue.setText("" + entry.value);
    }
}