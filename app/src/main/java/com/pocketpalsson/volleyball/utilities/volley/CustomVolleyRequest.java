package com.pocketpalsson.volleyball.utilities.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public abstract class CustomVolleyRequest<T> extends Request<T> {
    private static final String TAG = "Volley_Tag";
    private final VolleyQueue queue;
    public final String uuid;
    private final ResponseListener<T> listener;
    private Priority mPriority = Priority.LOW;


    public CustomVolleyRequest(VolleyQueue queue, String url, String uuid, boolean isHighPriority, ResponseListener<T> listener) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        this.queue = queue;
        this.uuid = uuid;
        this.listener = listener;
        setPriority(isHighPriority ? Priority.HIGH : Priority.NORMAL);
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public abstract T parseResponse(String response);

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        Log.d(TAG, "parseNetworkResponse-> uuid: " + uuid);
        String response = parseVolleyResponse(networkResponse);
        return Response.success(parseResponse(response), HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    @Override
    protected void deliverResponse(T value) {
        queue.modifyQueueCount(uuid, -1);
        if(listener != null){
            listener.responseReceived(value, queue.getRequestCount(uuid) == 0);
        }
        Log.d(TAG, "deliverResponse-> uuid: " + uuid);
    }

    public interface ResponseListener<T> {
        void responseReceived(T value, boolean isLastResponse);
    }

    public static String parseVolleyResponse(NetworkResponse response) {
        try {
            return new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                return new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e1) {
                return "";
            }
        }
    }
}
