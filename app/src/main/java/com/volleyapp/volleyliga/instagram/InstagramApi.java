package com.volleyapp.volleyliga.instagram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volleyapp.volleyliga.instagram.busEvents.InstagramAuthCancelledEvent;
import com.volleyapp.volleyliga.instagram.busEvents.InstagramAuthErrorEvent;
import com.volleyapp.volleyliga.instagram.busEvents.InstagramTokenReceivedEvent;
import com.volleyapp.volleyliga.instagram.gsonModels.GInstagramTagResponse;
import com.volleyapp.volleyliga.utilities.CustomBus;
import com.volleyapp.volleyliga.utilities.EpochDateConverter;
import com.squareup.otto.Subscribe;

import java.util.Date;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 */
public class InstagramApi {

    public static final String CLIENT_ID = "d70b7e83298e47c9afb2d8efdd16d53d";
    public static final String CLIENT_SECRET = "f761df595b224642b79ba8b70707e6d3";
    public static final String CALLBACK_URL = "http://volleyapp.dk/";
    private static final String SHARED = "Instagram_Preferences";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";
    public static final CustomBus eventBus = new CustomBus();
    public static final String BASE_URL = "https://api.instagram.com/v1/";
    private final InstagramService instagramService;


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public String userName, id, name, accessToken = "";
    private Context context;
    private OAuthAuthenticationListener listener;

    public InstagramApi(Context context) {
        this(context, null);
    }

    public InstagramApi(Context context, OAuthAuthenticationListener listener) {
        this.listener = listener;
        this.context = context;
        eventBus.register(this);
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new EpochDateConverter())
                .create();
        instagramService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(InstagramService.class);
        refresh();
    }

    @Subscribe
    public void instagramTokenReceived(InstagramTokenReceivedEvent event) {
        if (listener != null) {
            listener.onSuccess();
        }
        refresh();
    }

    @Subscribe
    public void instagramLoginCancelled(InstagramAuthCancelledEvent event) {
        if (listener != null) {
            listener.onCancelled();
        }
    }

    @Subscribe
    public void instagramLoginError(InstagramAuthErrorEvent event) {
        if (listener != null) {
            listener.onFail(event.description);
        }
    }


    public void authorize() {
        context.startActivity(new Intent(context, InstagramAuthActivity.class));
    }

    private void refresh() {
        userName = getUsername();
        id = getId();
        name = getName();
        accessToken = getAccessToken();
    }

    public void storeAccessToken(String accessToken, String id, String username, String name) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.commit();
    }

    public void resetAccessToken() {
        editor.putString(API_ID, "");
        editor.putString(API_NAME, "");
        editor.putString(API_ACCESS_TOKEN, "");
        editor.putString(API_USERNAME, "");
        editor.commit();
    }

    public boolean hasAccessToken() {
        return !accessToken.equalsIgnoreCase("");
    }

    private String getUsername() {
        return sharedPref.getString(API_USERNAME, "");
    }

    /**
     * @return
     */
    private String getId() {
        return sharedPref.getString(API_ID, "");
    }

    /**
     * @return
     */
    private String getName() {
        return sharedPref.getString(API_NAME, "");
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    private String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, "");
    }


    public interface OAuthAuthenticationListener {
        void onSuccess();

        void onFail(String error);

        void onCancelled();
    }

    public interface InstagramService {
        @GET("tags/{tagName}/media/recent")
        Observable<GInstagramTagResponse> getFirstPageForTag(@Path("tagName") String tagName, @Query("access_token") String accessToken);

        @GET("{path}")
        Observable<GInstagramTagResponse> getFirstPageFromUrl(@Path("path") String path);
    }

    public Observable<GInstagramTagResponse> getTagEntriesForTag(String tagName) {
        if (!hasAccessToken()) {
            return Observable.empty();
        }
        return instagramService.getFirstPageForTag(tagName, accessToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GInstagramTagResponse> getTagEntriesFromUrl(String url) {
        if (!hasAccessToken()) {
            return Observable.empty();
        }
        url = url.replace(BASE_URL, "");
        return instagramService.getFirstPageFromUrl(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}