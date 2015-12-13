package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.SetInfoModel;
import com.pocketpalsson.volleyball.utilities.ContextHelper;

import java.util.List;

import butterknife.ButterKnife;

public class SetSummaryView extends LinearLayout {

    public SetSummaryView(Context context) {
        super(context);
        init();
    }


    public SetSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.all_sets_view, this);
        setOrientation(HORIZONTAL);
        ButterKnife.bind(this);
    }

    public void setStats(List<SetInfoModel> stats) {
        removeAllViews();
        int leftMargin = 0;
        int rightMargin = ContextHelper.dpToPixels(getContext(), 2);
        for (SetInfoModel stat : stats) {
            SetView setView = new SetView(getContext());
            addView(setView);
            setView.setStat(stat);
            LayoutParams layoutParams = (LayoutParams) setView.getLayoutParams();
            layoutParams.setMargins(leftMargin, 0, rightMargin, 0);
            setView.setLayoutParams(layoutParams);
            leftMargin = rightMargin;
        }
    }
}
