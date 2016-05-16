package com.sportsapp.volleyliga.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchWithScoreView extends FrameLayout {

    @Bind(R.id.ivHomeTeam)
    public ImageView ivHomeTeam;
    @Bind(R.id.tvHomeTeamScore)
    public TextView tvHomeTeamScore;
    @Bind(R.id.ivGuestTeam)
    public ImageView ivGuestTame;
    @Bind(R.id.tvGuestTeamScore)
    public TextView tvGuestTeamScore;
    @Bind(R.id.tvMatchDate)
    public TextView tvMatchDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm", Locale.getDefault());

    public MatchWithScoreView(Context context) {
        super(context);
        init();
    }

    public MatchWithScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchWithScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.match_view_holder, this);
        ButterKnife.bind(this, this);
    }

    public void setMatchModel(MatchModel match, boolean addSelectorBackground) {
        int homeTypeface = match.setsWonByHome > match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;
        int guestTypeface = match.setsWonByHome < match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;
//        allSetsView.setStats(match.getSetList());

        if(addSelectorBackground) {
            setForeground(ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
        } else {
            setForeground(null);
        }

        ivHomeTeam.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamHome.logoRef));
        tvHomeTeamScore.setText("" + match.setsWonByHome);
        tvHomeTeamScore.setTypeface(null, homeTypeface);

        ivGuestTame.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamGuest.logoRef));
        tvGuestTeamScore.setTypeface(null, guestTypeface);
        tvGuestTeamScore.setText("" + match.setsWonByGuest);

        tvMatchDate.setText(dateFormat.format(match.matchDateTime));
    }
}
