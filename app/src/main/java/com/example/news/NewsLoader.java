package com.example.news;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private String mUrl;
    private Context ctx;
    private String mApiKey;

    /**
     * Constructor to create a AsyncTaskLoader object
     *
     * @param ct  of the app
     * @param url of the API to be used
     */

    public NewsLoader(Context ct, String url, String apiKey) {
        super(ct);
        ctx = ct;
        mUrl = url;
        mApiKey = apiKey;


    }

    @Nullable
    @Override
    public List<NewsItem> loadInBackground() {
        // Don't perform the request if the URL is null or empty string.
        if (mUrl == null | mUrl == "") {
            return null;
        }
        Log.v(LOG_TAG, "Inside the loadInBAckground method");
        //Call fetchNews data in QueryUtils class
        return QueryUtils.fetchNewsData(ctx, mUrl, mApiKey);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Inside the onStartLoading method");
        forceLoad();
    }
}
