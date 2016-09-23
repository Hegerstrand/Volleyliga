package com.volleyapp.volleyliga.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.MatchStatisticsModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.sweers.barber.Barber;

public class MatchStatsView extends LinearLayout {
    @Bind(R.id.attacks)
    public MatchStatsLineView attacks;
    @Bind(R.id.blocks)
    public MatchStatsLineView blocks;
    @Bind(R.id.serves)
    public MatchStatsLineView serves;
    @Bind(R.id.errors)
    public MatchStatsLineView errors;
    @Bind(R.id.receptionPercentage)
    public MatchStatsLineView receptionPercentage;
    public MatchStatisticsModel matchStats;


    public MatchStatsView(Context context) {
        super(context);
        init();
    }


    public MatchStatsView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public MatchStatsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.MatchStatsBar, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.match_stats_view, this);
        setOrientation(LinearLayout.VERTICAL);
        ButterKnife.bind(this);
    }

    public void setMatchStats(MatchStatisticsModel matchStats){
        this.matchStats = matchStats;
        attacks.setStat(matchStats.attacks);
        blocks.setStat(matchStats.blocks);
        serves.setStat(matchStats.serves);
        errors.setStat(matchStats.errors);
//        totalPoints.setEntry(matchStats.totalPoints);
        receptionPercentage.setStat(matchStats.receptionPercentage, "%");
    }
}
