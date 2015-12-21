package com.pocketpalsson.volleyball.views.controllers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.PlayerStatisticModelWrapper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayerStatisticViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvPlayerName)
    public TextView tvPlayerName;
    @Bind(R.id.tvValue)
    public TextView tvValue;

    public PlayerStatisticViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setEntry(PlayerStatisticModelWrapper entry) {
        int textColor = entry.isNegativeStat ? Color.parseColor("#B71C1C") : Color.BLACK;
        tvPlayerName.setText(entry.title);
        tvValue.setText("" + entry.value);
        tvValue.setTextColor(textColor);
    }
}