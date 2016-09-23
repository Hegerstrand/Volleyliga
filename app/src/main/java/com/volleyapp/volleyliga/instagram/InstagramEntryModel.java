package com.volleyapp.volleyliga.instagram;

import com.volleyapp.volleyliga.instagram.gsonModels.GInstagramEntry;

import java.util.Date;

public class InstagramEntryModel {
    public Date created;
    public String caption = "", userFullName = "", username = "", userProfilePictureUrl = "", link = "", thumbnailUrl = "", imageUrl = "";

    public InstagramEntryModel(GInstagramEntry gEntry) {
        caption = gEntry.caption.text;
        created = gEntry.created;
        link = gEntry.link;
        if (gEntry.images != null) {
            if (gEntry.images.thumbnail != null) {
                thumbnailUrl = gEntry.images.thumbnail.url;
            }
            if (gEntry.images.standardResolution != null) {
                imageUrl = gEntry.images.standardResolution.url;
            }
        }
        if (gEntry.user != null) {
            userFullName = gEntry.user.fullName;
            username = gEntry.user.username;
            userProfilePictureUrl = gEntry.user.profilePicture;
        }
    }
}
