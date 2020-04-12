package com.example.news;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    private String mUrl;
    private List<NewsItem> mData;

    /**
     * Constructor to create a AsyncTaskLoader object
     *
     * @param ct  of the app
     * @param url of the API to be used
     */

    public NewsLoader(Context ct, String url) {
        super(ct);
        mUrl = url;

    }

    @Nullable
    @Override
    public List<NewsItem> loadInBackground() {
        // Don't perform the request if the URL is null or empty string.
        if (mUrl == null | mUrl == "") {
            return null;
        }
        //Call fetchNews data in QueryUtils class
        mData = QueryUtils.fetchNewsData(mUrl);
        return mData;
    }

    @Override
    protected void onStartLoading() {

        if (mData != null) {
            //Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }

    }

    @Override
    public void deliverResult(@Nullable List<NewsItem> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
