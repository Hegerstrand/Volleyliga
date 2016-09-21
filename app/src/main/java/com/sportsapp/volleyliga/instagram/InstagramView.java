package com.volleyapp.volleyliga.instagram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.instagram.gsonModels.GInstagramEntry;
import com.volleyapp.volleyliga.instagram.gsonModels.GInstagramTagResponse;
import com.volleyapp.volleyliga.instagram.listAdapter.InstagramListAdapter;
import com.volleyapp.volleyliga.views.controllers.DividerItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class InstagramView extends LinearLayout {

    @Bind(R.id.instagramRecyclerView)
    public RecyclerView recyclerView;
    @Bind(R.id.instagramFeedContainer)
    public LinearLayout instagramFeedContainer;
    @Bind(R.id.instagramSignInPromptContainer)
    public LinearLayout signInPromptContainer;


    private InstagramApi instagramApi;
    private String nextMinId = "";
    private InstagramListAdapter adapter;
    private String hashtag;

    public InstagramView(Context context) {
        super(context);
        init();
    }

    public InstagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InstagramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.instagram_view, this);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        ButterKnife.bind(this);

        adapter = new InstagramListAdapter(getContext());
        adapter.setInstagramEntryClickListener(entry -> openEntry(entry));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        if(isInEditMode()){
            return;
        }
        instagramApi = new InstagramApi(getContext(), new InstagramApi.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                updateInstagramFeed();
            }

            @Override
            public void onFail(String error) {
            }

            @Override
            public void onCancelled() {
            }
        });

        updateInstagramFeed();
    }

    private void openEntry(InstagramEntryModel entry) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.link));
        getContext().startActivity(intent);
    }

    @OnClick(R.id.btnSignInWithInstagram)
    public void signInToInstagram(){
        if(instagramApi.hasAccessToken()){
            updateInstagramFeed();
        } else {
            instagramApi.authorize();
        }
    }

    private void updateInstagramFeed() {
        if(!instagramApi.hasAccessToken()){
            signInPromptContainer.setVisibility(View.VISIBLE);
            instagramFeedContainer.setVisibility(View.GONE);
            return;
        }
        signInPromptContainer.setVisibility(View.GONE);
        instagramFeedContainer.setVisibility(View.VISIBLE);
        instagramApi.getTagEntriesForTag("volleyappdk")
                .subscribe(new Subscriber<GInstagramTagResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(GInstagramTagResponse instagramTagResponse) {
                        tagResponseReceived(instagramTagResponse);
                    }
                });
    }

    private void tagResponseReceived(GInstagramTagResponse response) {
        nextMinId = response.pagination.nextMinId;
        if(response.entries != null){
            for (GInstagramEntry gEntry : response.entries) {
                adapter.addItem(new InstagramEntryModel(gEntry));
            }
        }
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
