package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, listItems.get(position).getmSourceURL());
                sendIntent.setType("text/plain");

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
        holder.itemCountView.setText(String.valueOf(position));
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
            holder.timeView.setText(currentItem.getmTimeString());
            holder.timeView.setVisibility(View.VISIBLE);
        } else {
            holder.timeView.setVisibility(View.GONE);
        }

        if (currentItem.getmImageURL() != null) {
            ImageLoadAsyncTask task = new ImageLoadAsyncTask(holder.sourceImage);
            task.execute(currentItem.getmImageURL());
            holder.sourceImage.setVisibility(View.VISIBLE);
        } else {
            holder.sourceImage.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setData(List<NewsItem> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
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
            //shareImage = v.findViewById(R.id.share_image) ;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ConstraintLayout) {
                mListener.onView(v, getAdapterPosition());
            } else if (v instanceof ImageView) {
                mListener.share((ImageView) v, getAdapterPosition());
            }

        }

        public interface RecyclerViewClickListener {

            void onView(View v, int position);

            void share(ImageView v, int position);

        }

    }

    private static class ImageLoadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImage;


        private ImageLoadAsyncTask(ImageView image) {
            mImage = image;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openStream());
            } catch (MalformedURLException m) {
                Log.e("ImageLoadAsyncTask", "Malformed exception in image URL");
            } catch (IOException e) {
                Log.e("ImageLoadAsyncTask", "Cannot open the URL");
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);

        }
    }


}
