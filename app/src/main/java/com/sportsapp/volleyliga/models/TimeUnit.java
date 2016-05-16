package com.sportsapp.volleyliga.models;

import android.support.annotation.StringRes;

import com.sportsapp.volleyliga.R;

public enum TimeUnit {
    Minutes,
    Hours,
    Days,;

    @StringRes
    public static int getName(TimeUnit timeUnit) {
        switch (timeUnit) {
            case Minutes:
                return R.string.minutes;
            case Hours:
                return R.string.hours;
            case Days:
                return R.string.days;
        }
        return R.string.empty;
    }
}
