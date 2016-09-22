package com.volleyapp.volleyliga.views.controllers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.views.MatchWithScoreView;

public class MatchWithScoreViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {


    private final MatchWithScoreView matchView;
    private ClickListener clickListener;
    private MatchModel match;

    public MatchWithScoreViewHolder(MatchWithScoreView itemView) {
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
        clickListener.onClick(match);
    }

//    @Override
//    public boolean onLongClick(View v) {
//
//         If long clicked, passed last variable as true.
//        clickListener.onClick(v, getPosition(), true);
//        return true;
//    }
}