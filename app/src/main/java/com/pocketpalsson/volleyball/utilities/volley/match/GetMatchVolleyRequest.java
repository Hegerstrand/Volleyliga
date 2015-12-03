package com.pocketpalsson.volleyball.utilities.volley.match;

import com.pocketpalsson.volleyball.models.MatchModel;
import com.pocketpalsson.volleyball.utilities.volley.CustomVolleyRequest;
import com.pocketpalsson.volleyball.utilities.volley.VolleyQueue;

public class GetMatchVolleyRequest extends CustomVolleyRequest<MatchModel> {
    public GetMatchVolleyRequest(VolleyQueue queue, String url, String uuid, boolean isHighPriority, ResponseListener<MatchModel> listener) {
        super(queue, url, uuid, isHighPriority, listener);
    }

    @Override
    public MatchModel parseResponse(String response) {
        MatchXmlPullParser parser = new MatchXmlPullParser();
        return parser.parse(response);
    }
}
