package com.volleyapp.volleyliga.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.StatisticModel;

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
    private String suffix = "";

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
        leftValue.setText("" + stat.homeStat + suffix);
        rightValue.setText("" + stat.guestStat + suffix);
//        leftValue.setTextColor(stat.homeStat == stat.maxValue ? ContextCompat.getColor(getContext(), R.color.accent) : Color.GRAY);
//        rightValue.setTextColor(stat.guestStat == stat.maxValue ? ContextCompat.getColor(getContext(), R.color.accent) : Color.GRAY);
        leftValue.setTypeface(null, stat.homeStat == stat.maxValue ? Typeface.BOLD : Typeface.NORMAL);
        rightValue.setTypeface(null, stat.guestStat == stat.maxValue ? Typeface.BOLD : Typeface.NORMAL);
        leftBar.setValues(stat.homeStat, stat.maxValue, stat.absoluteMaxValue);
        rightBar.setValues(stat.guestStat, stat.maxValue, stat.absoluteMaxValue);
    }

    public void setStat(StatisticModel stat, String suffix) {
        this.suffix = suffix;
        setStat(stat);
    }
}
