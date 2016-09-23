package com.volleyapp.volleyliga.instagram.gsonModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GInstagramTagResponse {

    public GPaginationInfo pagination;
    @SerializedName("data")
    public List<GInstagramEntry> entries;

    public class GPaginationInfo {
        @SerializedName("next_min_id")
        public String nextMinId;
    }


}
