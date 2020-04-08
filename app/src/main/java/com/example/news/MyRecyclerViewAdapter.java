package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        return new ViewHolder(view, new ViewHolder.RecyclerViewClickListener() {
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

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsItem currentItem = listItems.get(position);
        //Assign the values of your ArrayList to the assigned holder views
        holder.itemCountView.setText((position + 1 + "."));
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        String category = sharedPrefs.getString(ctx.getString(R.string.settings_headlines_category_key),
                ctx.getString(R.string.settings_headlines_category_default));
        holder.categoryTextView.setText(category);
        if (currentItem.getmTimeString() != null) {
            holder.timeView.setText(getTime(currentItem.getmTimeString()));
            holder.timeView.setVisibility(View.VISIBLE);
        } else {
            holder.timeView.setVisibility(View.GONE);
        }

        Glide.with(ctx).load(currentItem.getmImageURL()).placeholder(new ColorDrawable(Color.BLACK)).error(R.drawable.error_image).centerCrop().into(holder.sourceImage);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setData(List<NewsItem> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    private String getTime(String timeString) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setLenient(true);
        String timeText = "";

        try {
            Date date = simpleDateFormat.parse(timeString);
            long milliseconds = date.getTime();
            timeText = DateUtils.getRelativeTimeSpanString(milliseconds, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

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


        public ViewHolder(@NonNull View v, RecyclerViewClickListener listener) {
            super(v);
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
