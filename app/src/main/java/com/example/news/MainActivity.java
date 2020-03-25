package com.example.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Base URL to fetch data from NEWSAPI
     */
    private static final String BASE_REQUEST_URL = "http://newsapi.org/v2/top-headlines?country=IN&apiKey=2b66531296074666bd3fbd6c03dce4c1";
    //private static final String API_KEY = "2b66531296074666bd3fbd6c03dce4c1";
    /**
     * Adapter for the list of news items
     */
    private MyRecyclerViewAdapter adapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Check whether the phone is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //find a reference to the Progress bar
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        RecyclerView newsList = findViewById(R.id.recycler_view);
        //use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);

        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the recyclerVIew
        // so the list can be populated in the user interface

        newsList.setAdapter(adapter);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        if (isConnected) {
            getSupportLoaderManager().initLoader(1, null, this);
            newsList.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);

        } else {
            loadingSpinner.setVisibility(View.GONE);
            newsList.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);

        }
    }

    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, @Nullable Bundle args) {

        Uri baseUri = Uri.parse(BASE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //uriBuilder.appendPath("top-headlines");
        //uriBuilder.appendQueryParameter("country", "IN");
        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> data) {

        // If there is a valid list of news items, then add them to the adapter's
        // data set. This will trigger the recyclerView to update.
        if (data != null && !data.isEmpty()) {
            adapter.setData(data);
            loadingSpinner.setVisibility(View.GONE);
        } else {
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText("No news items are found");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsItem>> loader) {

        // Loader reset, so we can clear out our existing data.
        adapter.setData(null);

    }
}
