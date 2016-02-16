package com.sportsapp.volleyliga.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.SetInfoModel;
import com.sportsapp.volleyliga.utilities.ContextHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetFullDetailView extends LinearLayout {
    @Bind(R.id.setContainer)
    public LinearLayout setContainer;

    public SetFullDetailView(Context context) {
        super(context);
        init();
    }


    public SetFullDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetFullDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.set_full_detail_view, this);
        ButterKnife.bind(this);
    }

    public void setStats(List<SetInfoModel> stats) {
        setContainer.removeAllViews();
        int topMargin = 0;
        int bottomMargin = ContextHelper.dpToPixels(getContext(), 4);
        for (SetInfoModel stat : stats) {
            LargeSetView setView = new LargeSetView(getContext());
            setView.setGravity(stat.homeWon ? Gravity.LEFT : Gravity.RIGHT);
            setContainer.addView(setView);
            setView.setStat(stat);
            LayoutParams layoutParams = (LayoutParams) setView.getLayoutParams();
            layoutParams.setMargins(0, topMargin, 0, bottomMargin);
            setView.setLayoutParams(layoutParams);
            topMargin = bottomMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Adjust width as necessary
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mBoundedWidth = ContextHelper.dpToPixels(getContext(), 150);
        if(mBoundedWidth > 0 && mBoundedWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mBoundedWidth, measureMode);
        }
        // Adjust height as necessary
//        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
//        if(mBoundedHeight > 0 && mBoundedHeight < measuredHeight) {
//            int measureMode = MeasureSpec.getMode(heightMeasureSpec);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mBoundedHeight, measureMode);
//        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
