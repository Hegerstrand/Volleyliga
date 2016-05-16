package com.sportsapp.volleyliga.views.controllers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.views.FutureMatchView;

public class MatchFutureViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private final FutureMatchView matchView;
    private ClickListener clickListener;
    private MatchModel match;

    public MatchFutureViewHolder(FutureMatchView itemView) {
        super(itemView);
        matchView = itemView;
        // We set listeners to the whole item view, but we could also
        // specify listeners for the title or the icon.
        itemView.setOnClickListener(this);
    }

    public void setMatchModel(MatchModel match) {
        this.match = match;
        matchView.setMatchModel(match, true);
    }


    public interface ClickListener {
        void onClick(MatchModel position);
    }

    /* Setter for listener. */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(match);
        }
    }
}