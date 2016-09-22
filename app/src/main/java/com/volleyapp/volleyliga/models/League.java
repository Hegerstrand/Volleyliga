package com.volleyapp.volleyliga.models;

public enum League {
    MALE,
    FEMALE,
    UNKNOWN,;

    public static int getIndex(League league) {
        switch(league){
            case MALE:
                return 0;
            case FEMALE:
                return 1;
        }
        return 0;
    }
}
