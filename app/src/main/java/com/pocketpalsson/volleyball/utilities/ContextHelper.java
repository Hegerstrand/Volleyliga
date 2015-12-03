package com.pocketpalsson.volleyball.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class ContextHelper {

    public static int dpToPixels(Context context, int dps){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                r.getDisplayMetrics()
        );
    }
}
