package com.sportsapp.volleyliga.views.controllers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.PlayerStatisticModelWrapper;

public class PlayerStatisticViewHolder extends RecyclerView.ViewHolder {

    public TextView tvPlayerName;
    public TextView tvPlayerNumber;
    public TextView tvValue;

    public PlayerStatisticViewHolder(View itemView) {
        super(itemView);
        tvPlayerName = (TextView) itemView.findViewById(R.id.tvPlayerName);
        tvPlayerNumber = (TextView) itemView.findViewById(R.id.tvPlayerNumber);
        tvValue = (TextView) itemView.findViewById(R.id.tvValue);
    }

    public void setEntry(PlayerStatisticModelWrapper entry) {
        int textColor = entry.isNegativeStat ? Color.parseColor("#B71C1C") : Color.BLACK;
        tvPlayerName.setText(entry.title);
        tvPlayerNumber.setText(entry.playerNumber);
        tvValue.setText("" + entry.value);
        tvValue.setTextColor(textColor);
    }
}