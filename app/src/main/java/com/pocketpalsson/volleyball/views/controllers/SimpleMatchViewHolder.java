package com.pocketpalsson.volleyball.views.controllers;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.MatchModel;
import com.pocketpalsson.volleyball.views.AllSetsView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SimpleMatchViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    @Bind(R.id.allSetsView)
    public AllSetsView allSetsView;
    @Bind(R.id.tvHomeTeam)
    public TextView tvHomeTeam;
    @Bind(R.id.ivHomeTeam)
    public ImageView ivHomeTeam;
    @Bind(R.id.tvHomeTeamScore)
    public TextView tvHomeTeamScore;
    @Bind(R.id.tvGuestTeam)
    public TextView tvGuestTame;
    @Bind(R.id.ivGuestTeam)
    public ImageView ivGuestTame;
    @Bind(R.id.tvGuestTeamScore)
    public TextView tvGuestTeamScore;
    @Bind(R.id.tvMatchDate)
    public TextView tvMatchDate;


    private ClickListener clickListener;
    private MatchModel match;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm", Locale.getDefault());

    public SimpleMatchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // We set listeners to the whole item view, but we could also
        // specify listeners for the title or the icon.
        itemView.setOnClickListener(this);
    }

    public void setMatchModel(MatchModel match) {
        this.match = match;
        int homeTypeface = match.setsWonByHome > match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;
        int guestTypeface = match.setsWonByHome < match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;
        allSetsView.setStats(match.getSetList());

        ivHomeTeam.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), match.teamHome.logoRef));
        tvHomeTeam.setText("" + match.teamHome.name);
        tvHomeTeamScore.setText("" + match.setsWonByHome);
        tvHomeTeam.setTypeface(null, homeTypeface);
        tvHomeTeamScore.setTypeface(null, homeTypeface);

        ivGuestTame.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), match.teamGuest.logoRef));
        tvGuestTame.setText("" + match.teamGuest.name);
        tvGuestTame.setTypeface(null, guestTypeface);
        tvGuestTeamScore.setTypeface(null, guestTypeface);
        tvGuestTeamScore.setText("" + match.setsWonByGuest);

        tvMatchDate.setText(dateFormat.format(match.matchDateTime.getTime()));
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