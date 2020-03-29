package com.example.news;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.loader.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class encompassing the helper methods related to requesting and receiving the news data from News API
 */

public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private QueryUtils() {
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
            Log.e(LOG_TAG, "this is the created URL" + url.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "An error occured in createUrl method");
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHtpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            //urlConnection.setRequestProperty("x-api-key", apiKey);
            urlConnection.connect();
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Bad HTTP response from URL requested :" + urlConnection.getResponseCode());

            }
        } catch (IOException io) {
            Log.e(LOG_TAG, "Problem retrieving JSOn results :" + io);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */

    public static List<NewsItem> extractNewsItems(String jsonresponse) {

        //Create an empty ArrayList of newitems that we can add to
        List<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message

        try {
            // build up a list of Earthquake objects with the corresponding data
            JSONObject rootNode = new JSONObject(jsonresponse);
            JSONArray articlesArray = rootNode.getJSONArray("articles");

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject jsonObject = articlesArray.getJSONObject(i);
                JSONObject sourceDetails = jsonObject.getJSONObject("source");
                String sourceName = sourceDetails.getString("name");
                String newsTitle = jsonObject.getString("title");
                String sourceUrl = jsonObject.getString("url");
                String imageUrl = jsonObject.getString("urlToImage");
                String timePublished = jsonObject.getString("publishedAt");
                NewsItem newsItem = new NewsItem(sourceName, newsTitle, timePublished, imageUrl, sourceUrl);
                newsItems.add(newsItem);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSOn response inside extractNewsItems method");
        }

        return newsItems;
    }

    /**
     * Query the News API and return an ArrayList of NewsItem objects
     */

    public static List<NewsItem> fetchNewsData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);
        //Perform HTTP request to URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHtpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making Http request");
        }

        List<NewsItem> newsItems = extractNewsItems(jsonResponse);

        return newsItems;
    }

}




