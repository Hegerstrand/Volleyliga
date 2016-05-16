package com.sportsapp.volleyliga.instagram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.instagram.busEvents.InstagramAuthCancelledEvent;
import com.sportsapp.volleyliga.instagram.busEvents.InstagramAuthErrorEvent;
import com.sportsapp.volleyliga.instagram.busEvents.InstagramTokenReceivedEvent;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

public class InstagramAuthActivity extends Activity {

    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TAG = "InstagramAPI";

    @Bind(R.id.webView)
    public WebView webView;
    @Bind(R.id.loadingContainer)
    public RelativeLayout loadingContainer;

    private ProgressDialog mProgress;
    private InstagramApi instagramApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_auth);
        ButterKnife.bind(this);
        instagramApi = new InstagramApi(this);

        String authUrl = AUTH_URL + "?client_id=" + InstagramApi.CLIENT_ID + "&redirect_uri="
                + InstagramApi.CALLBACK_URL + "&response_type=code&display=touch&scope=public_content";

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.GONE);
        webView.loadUrl(authUrl);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        InstagramApi.eventBus.post(new InstagramAuthCancelledEvent());
        super.onBackPressed();
    }

    private void codeReceived(String code) {
        mProgress.setMessage("Processing...");
        mProgress.show();

        Observable.just(code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(this::getAccessToken);
    }

    private void getAccessToken(String code) {
        try {
            URL url = new URL(TOKEN_URL);
            Log.i(TAG, "Opening Token URL " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("client_id=" + InstagramApi.CLIENT_ID +
                    "&client_secret=" + InstagramApi.CLIENT_SECRET +
                    "&grant_type=authorization_code" +
                    "&redirect_uri=" + InstagramApi.CALLBACK_URL +
                    "&code=" + code);
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());
            Log.i(TAG, "response " + response);
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

            String accessToken = jsonObj.getString("access_token");
            Log.i(TAG, "Got access token: " + accessToken);

            String id = jsonObj.getJSONObject("user").getString("id");
            String user = jsonObj.getJSONObject("user").getString("username");
            String name = jsonObj.getJSONObject("user").getString("full_name");

            instagramApi.storeAccessToken(accessToken, id, user, name);
            InstagramApi.eventBus.post(new InstagramTokenReceivedEvent());
            finish();
        } catch (Exception e) {
            InstagramApi.eventBus.post(new InstagramAuthErrorEvent("Error retrieving Instagram access"));
        }
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(InstagramApi.CALLBACK_URL)) {
                String urls[] = url.split("=");
                codeReceived(urls[1]);
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            InstagramApi.eventBus.post(new InstagramAuthErrorEvent(description));
            finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            loadingContainer.setVisibility(View.GONE);
        }

    }

}
