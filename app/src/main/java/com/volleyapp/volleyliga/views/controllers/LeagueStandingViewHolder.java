package com.volleyapp.volleyliga.views.controllers;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.volleyapp.volleyliga.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.volleyapp.volleyliga.models.LeagueStandingModel;


public class LeagueStandingViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    @Bind(R.id.tvTeamName)
    public TextView tvTeamName;
    @Bind(R.id.ivTeam)
    public ImageView ivTeam;
    @Bind(R.id.tvPoints)
    public TextView tvPoints;
    @Bind(R.id.tvWonGames)
    public TextView tvWonGames;
    @Bind(R.id.tvLostGames)
    public TextView tvLostGames;


    private ClickListener clickListener;
    private LeagueStandingModel leagueStanding;

    public LeagueStandingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // We set listeners to the whole item view, but we could also
        // specify listeners for the title or the icon.
        itemView.setOnClickListener(this);
    }

    public void setLeagueStandingModel(LeagueStandingModel input) {
        this.leagueStanding = input;
        tvTeamName.setText(leagueStanding.teamName);
        ivTeam.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), leagueStanding.getTeam().logoRef));
        tvPoints.setText("Points" + ": " + leagueStanding.points);
        tvWonGames.setText("" + leagueStanding.matchesWon);
        tvLostGames.setText("Lost" + ": " + leagueStanding.matchesLost);
    }


    public interface ClickListener {
        void onClick(LeagueStandingModel position);
    }

    /* Setter for listener. */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(leagueStanding);
    }

//    @Override
//    public boolean onLongClick(View v) {
//
//         If long clicked, passed last variable as true.
//        clickListener.onClick(v, getPosition(), true);
//        return true;
//    }
}