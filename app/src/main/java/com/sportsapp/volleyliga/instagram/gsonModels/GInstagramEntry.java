package com.volleyapp.volleyliga.instagram.gsonModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class GInstagramEntry {
    @SerializedName("created_time")
    public Date created;
    public String link;

    public GInstagramImageContainer images;

    public GInstagramEntryCaption caption;

    public GInstagramUser user;

    public class GInstagramImageContainer {
        @SerializedName("low_resolution")
        public GInstagramImage lowResolution;
        @SerializedName("standard_resolution")
        public GInstagramImage standardResolution;
        public GInstagramImage thumbnail;
    }

    public class GInstagramImage {
        public String url;
        public int width, height;
    }

    public class GInstagramEntryCaption {
        public String text;
    }

    public class GInstagramUser {
        public String username;

        @SerializedName("profile_picture")
        public String profilePicture;
        @SerializedName("full_name")
        public String fullName;
    }
}
