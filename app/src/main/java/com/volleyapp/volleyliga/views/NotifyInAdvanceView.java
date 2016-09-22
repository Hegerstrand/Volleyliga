package com.volleyapp.volleyliga.views;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.TimeUnit;
import com.volleyapp.volleyliga.utilities.Preferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotifyInAdvanceView extends LinearLayout {

    @Bind(R.id.spTimeLengths)
    public Spinner spTimeUnits;
    @Bind(R.id.number)
    public EditText number;
    @Bind(R.id.swNotifyInAdvance)
    public Switch swNotifyInAdvance;


    public NotifyInAdvanceView(Context context) {
        super(context);
        inflate(getContext(), R.layout.notify_in_advance_view, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
        boolean shouldNotify = Preferences.with(getContext()).getShouldNotifyInAdvance();
        int notifyInAdvanceTimeLength = Preferences.with(getContext()).getNotifyInAdvanceTimeLength();
        TimeUnit notifyInAdvanceTimeUnit = Preferences.with(getContext()).getNotifyInAdvanceTimeUnit();
        swNotifyInAdvance.setChecked(shouldNotify);
        number.setEnabled(shouldNotify);
        number.setText("" + notifyInAdvanceTimeLength);
        number.setSelection(number.getText().length());
        spTimeUnits.setEnabled(shouldNotify);
        spTimeUnits.setSelection(notifyInAdvanceTimeUnit.ordinal());

        swNotifyInAdvance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            number.setEnabled(isChecked);
            spTimeUnits.setEnabled(isChecked);
        });
    }

    @OnClick(R.id.receiveNotifications)
    public void notifyInAdvanceClicked() {
        swNotifyInAdvance.toggle();
    }

    public void persist() {
        Preferences prefs = Preferences.with(getContext());
        prefs.setShouldNotifyInAdvance(swNotifyInAdvance.isChecked());
        prefs.setNotifyInAdvanceTimeLength(Integer.parseInt(number.getText().toString()));
        prefs.setNotifyInAdvanceTimeUnit(TimeUnit.values()[spTimeUnits.getSelectedItemPosition()]);
    }
}
