package com.sportsapp.volleyliga.utilities;

import com.squareup.otto.Bus;

public class BusProvider {
    private static final Bus BUS = new CustomBus();
    private static final Bus TIME_BUS = new CustomBus();

    private BusProvider() {}

    public static Bus getInstance() {
        return BUS;
    }

    public static Bus getTimeInstance(){
        return TIME_BUS;
    }
}
