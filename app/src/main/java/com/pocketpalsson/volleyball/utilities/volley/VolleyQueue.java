package com.pocketpalsson.volleyball.utilities.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.ConcurrentHashMap;

public class VolleyQueue {
    public static VolleyQueue instance;
    private RequestQueue requestQueue;
    private final ConcurrentHashMap<String, Integer> requestCount = new ConcurrentHashMap<String, Integer>();
    private Context context;

    public static void setup(Context context) {
        VolleyQueue.instance = new VolleyQueue(context);
    }

    public VolleyQueue(Context context) {
        this.context = context;
    }

    public RequestQueue getQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public synchronized void add(CustomVolleyRequest request) {
        getQueue().add(request);
        modifyQueueCount(request.uuid, 1);
    }

    public synchronized void modifyQueueCount(String uuid, int number) {
        synchronized (requestCount) {
            if (!requestCount.containsKey(uuid)) {
                requestCount.put(uuid, 0);
            }
            requestCount.put(uuid, requestCount.get(uuid) + number);
        }
    }

    public synchronized int getRequestCount(String uuid) {
        synchronized (requestCount) {
            if (requestCount.containsKey(uuid)) {
                return requestCount.get(uuid);
            }
            return 0;
        }
    }
}
