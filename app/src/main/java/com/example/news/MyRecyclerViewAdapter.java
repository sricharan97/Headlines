package com.example.news;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    //Member variables
    private Context ctx;
    private List<NewsItem> listItems;

    //Constructor

    public MyRecyclerViewAdapter(Context ct, List<NewsItem> items) {

        ctx = ct;
        listItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(view, new ViewHolder.RecyclerViewClickListener() {
            @Override
            public void onView(View v, int position) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(listItems.get(position).getmSourceURL()));
                v.getContext().startActivity(intent);
            }

            @Override
            public void share(ImageView v, int position) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, listItems.get(position).getmSourceURL());
                Intent shareIntent = Intent.createChooser(sendIntent, "Headlines");
                v.getContext().startActivity(shareIntent);

            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsItem currentItem = listItems.get(position);
        //Assign the values of your ArrayList to the assigned holder views
        holder.itemCountView.setText(String.valueOf(position + 1 + "."));
        if (currentItem.getmSourceName() != null) {
            holder.sourceNameView.setText(currentItem.getmSourceName());
            holder.sourceNameView.setVisibility(View.VISIBLE);
        } else {
            holder.sourceNameView.setVisibility(View.GONE);
        }

        if (currentItem.getmNewsTitle() != null) {
            holder.titleTextView.setText(currentItem.getmNewsTitle());
            holder.titleTextView.setVisibility(View.VISIBLE);
        } else {
            holder.titleTextView.setVisibility(View.GONE);
        }
        holder.categoryTextView.setText("Headlines");
        if (currentItem.getmTimeString() != null) {
            holder.timeView.setText(getTime(currentItem.getmTimeString()));
            holder.timeView.setVisibility(View.VISIBLE);
        } else {
            holder.timeView.setVisibility(View.GONE);
        }

        Glide.with(ctx).load(currentItem.getmImageURL()).placeholder(new ColorDrawable(Color.BLACK)).error(R.drawable.errorimage).centerCrop().into(holder.sourceImage);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setData(List<NewsItem> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    public String getTime(String timeString) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setLenient(true);
        String timeText = "";

        try {
            Date date = simpleDateFormat.parse(timeString);
            long milliseconds = new Date().getTime() - date.getTime();
            int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
            int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
            if (hours > 0) {
                if (hours == 1)
                    timeText = "1 hour ago";
                else if (hours < 24)
                    timeText = String.valueOf(hours) + " hours ago";
                else {
                    int days = (int) Math.ceil(hours % 24);
                    if (days == 1)
                        timeText = "1 day ago";
                    else
                        timeText = String.valueOf(days) + " days ago";
                }
            } else {
                if (minutes == 0)
                    timeText = "less than 1 minute ago";
                else if (minutes == 1)
                    timeText = "1 minute ago";
                else
                    timeText = String.valueOf(minutes) + " minutes ago";
            }

        } catch (ParseException e) {
            Log.e("time string parsing", e.toString());
        }
        return timeText;

    }


    /**
     * define a static class {@link ViewHolder} which has the information on how to handle
     * a single item in the list
     */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        private TextView itemCountView;
        private TextView sourceNameView;
        private TextView titleTextView;
        private TextView categoryTextView;
        private TextView timeView;
        private ImageView sourceImage;
        private ImageView shareImage;
        //private ImageView shareImage;
        private ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View v, RecyclerViewClickListener listener) {
            super(v);
            constraintLayout = v.findViewById(R.id.constraint_layout);
            mListener = listener;
            itemCountView = v.findViewById(R.id.item_count);
            sourceNameView = v.findViewById(R.id.source_name);
            titleTextView = v.findViewById(R.id.title_text);
            categoryTextView = v.findViewById(R.id.category_text);
            timeView = v.findViewById(R.id.time_text);
            sourceImage = v.findViewById(R.id.source_image);
            shareImage = v.findViewById(R.id.share_image);
            v.setOnClickListener(this);
            shareImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == shareImage.getId()) {
                mListener.share((ImageView) v, getAdapterPosition());
            } else if (v instanceof ConstraintLayout) {
                mListener.onView(v, getAdapterPosition());
            }


        }

        public interface RecyclerViewClickListener {

            void onView(View v, int position);

            void share(ImageView v, int position);

        }

    }




}
