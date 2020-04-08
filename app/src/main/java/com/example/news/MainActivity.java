package com.example.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Base URL to fetch data from NEWSAPI
     */
    private static final String BASE_REQUEST_URL = "http://newsapi.org/v2/top-headlines";
    //private static final String API_KEY = "47c82aa4c9a34f219581c5b171b8c2dc";
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
        loadingSpinner = findViewById(R.id.loading_spinner);

        RecyclerView newsList = findViewById(R.id.recycler_view);
        //use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);
        //add horizontal line between the items in recyclerview
        ListItemDivider itemDecor = new ListItemDivider(this, LinearLayoutManager.VERTICAL, 48, 16);
        newsList.addItemDecoration(itemDecor);

        adapter = new MyRecyclerViewAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the recyclerVIew
        // so the list can be populated in the user interface

        newsList.setAdapter(adapter);

        mEmptyStateTextView = findViewById(R.id.empty_view);

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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String country = sharedPrefs.getString(getString(R.string.settings_headlines_country_key),
                getString(R.string.settings_headlines_country_default));

        String category = sharedPrefs.getString(getString(R.string.settings_headlines_category_key),
                getString(R.string.settings_headlines_category_default));
        String pageSize = sharedPrefs.getString(getString(R.string.page_size_key), getString(R.string.page_size_default));

        Uri baseUri = Uri.parse(BASE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("country", country);
        uriBuilder.appendQueryParameter("category", category);
        uriBuilder.appendQueryParameter("pageSize", pageSize);

        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> data) {

        // If there is a valid list of news items, then add them to the adapter's
        // data set. This will trigger the recyclerView to update.
        if (data != null && !data.isEmpty()) {
            Log.e("inside the if condition", data.toString());
            adapter.setData(data);
            loadingSpinner.setVisibility(View.GONE);
        } else {
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(getString(R.string.empty_state_text));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsItem>> loader) {

        // Loader reset, so we can clear out our existing data.
        adapter.setData(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
