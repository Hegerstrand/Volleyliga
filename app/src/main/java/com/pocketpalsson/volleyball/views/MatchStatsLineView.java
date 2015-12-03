package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.StatisticModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchStatsLineView extends LinearLayout {
    @Bind(R.id.leftBar)
    public MatchStatsBar leftBar;
    @Bind(R.id.rightBar)
    public MatchStatsBar rightBar;
    @Bind(R.id.tvLabel)
    public TextView label;
    @Bind(R.id.tvLeftValue)
    public TextView leftValue;
    @Bind(R.id.tvRightValue)
    public TextView rightValue;

    public MatchStatsLineView(Context context) {
        super(context);
        init();
    }


    public MatchStatsLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchStatsLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.match_stats_line_view, this);
        ButterKnife.bind(this);
    }

    public void setStat(StatisticModel stat) {
        label.setText(stat.name);
        leftValue.setText("" + stat.homeStat);
        rightValue.setText("" + stat.guestStat);
        leftValue.setTextColor(stat.isHomeValueMax ? ContextCompat.getColor(getContext(), R.color.accent) : Color.GRAY);
        rightValue.setTextColor(stat.isHomeValueMax ? Color.GRAY : ContextCompat.getColor(getContext(), R.color.accent));
        leftBar.setValues(stat.homeStat, stat.maxValue);
        rightBar.setValues(stat.guestStat, stat.maxValue);
    }
}
