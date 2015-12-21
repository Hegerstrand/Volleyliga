package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.Required;
import io.sweers.barber.StyledAttr;

public class MenuItem extends FrameLayout {


    @StyledAttr(value = R.styleable.MenuItem_itemIcon, kind = Kind.RES_ID)
    public int iconRef;
    @Required
    @StyledAttr(R.styleable.MenuItem_itemTitle)
    public String title;
    @Required
    @StyledAttr(R.styleable.MenuItem_itemValue)
    public String value;
    @Required
    @StyledAttr(R.styleable.MenuItem_includeArrow)
    public boolean includeArrow;

    @Bind(R.id.tvTitle)
    public TextView tvTitle;
    @Bind(R.id.tvValue)
    public TextView tvValue;
    @Bind(R.id.arrow)
    public View arrow;
    @Bind(R.id.icon)
    public View iconView;


    public MenuItem(Context context) {
        super(context);
        init();
    }

    public MenuItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.MenuItem, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.menu_item, this);
        ButterKnife.bind(this);

        setClickable(true);
        setForeground(ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));

        updateUI();
    }

    public int getIconRef() {
        return iconRef;
    }

    public void setIconRef(int iconRef) {
        this.iconRef = iconRef;
        updateUI();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateUI();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        updateUI();
    }

    public boolean isIncludeArrow() {
        return includeArrow;
    }

    public void setIncludeArrow(boolean includeArrow) {
        this.includeArrow = includeArrow;
        updateUI();
    }

    private void updateUI() {
        tvTitle.setText(title);
        tvValue.setText(value);
        arrow.setVisibility(includeArrow ? View.VISIBLE : View.GONE);
        if(iconRef > 0) {
            iconView.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), iconRef));
        }
    }
}
