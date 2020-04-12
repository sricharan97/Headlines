package com.example.news;

public class NewsItem {

    /**
     * Member variables
     */

    //Variable to hold the source name of the news
    private String mSourceName;

    //variable to hold the news title
    private String mNewsTitle;

    //variable to hold the published time of the story
    private String mTimeString;

    //variable to hold the url to image
    private String mImageURL;

    //variable to hold the url to source domain
    private String mSourceURL;

    /**
     * Creates a new newsItem object
     *
     * @param source    indicates the name of the news source
     * @param title     indicates the news title
     * @param time      indicates the date and time of news published
     * @param imageUrl  indicates the url to image
     * @param sourceUrl indicates the url to the news source
     */

    public NewsItem(String source, String title, String time, String imageUrl, String sourceUrl) {
        mSourceName = source;
        mNewsTitle = title;
        mTimeString = time;
        mImageURL = imageUrl;
        mSourceURL = sourceUrl;
    }


    public String getmSourceName() {
        return mSourceName;
    }

    public String getmNewsTitle() {
        return mNewsTitle;
    }

    public String getmTimeString() {
        return mTimeString;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public String getmSourceURL() {
        return mSourceURL;
    }
}
