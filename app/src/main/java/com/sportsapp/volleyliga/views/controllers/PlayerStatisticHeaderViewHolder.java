package com.sportsapp.volleyliga.views.controllers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.PlayerStatisticModelWrapper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayerStatisticHeaderViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvTitle)
    public TextView tvTitle;
    @Bind(R.id.tvValue)
    public TextView tvValue;


    public PlayerStatisticHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setEntry(PlayerStatisticModelWrapper entry) {
        int textColor = entry.isNegativeStat ? Color.parseColor("#B71C1C") : Color.BLACK;
        tvTitle.setTextColor(textColor);
        tvTitle.setText(entry.title);
        tvValue.setTextColor(textColor);
        tvValue.setText("" + entry.value);
    }
}