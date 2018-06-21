package org.aerogear.apps.memeolist.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 6/29/15.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Holder> {
    private final List<String> imageUrls;

    private final Bus bus;
    private final Picasso picasso;

    public ImagesAdapter(Context applicationContext, Bus bus, Picasso picasso) {
        this.bus = bus;
        this.picasso = picasso;

        InputStream memeStream = applicationContext.getResources().openRawResource(R.raw.meme);
        BufferedReader memeReader = new BufferedReader(new InputStreamReader(memeStream));
        String line;
        imageUrls = new ArrayList<>();

        try {
            while ((line = memeReader.readLine()) != null) {
                imageUrls.add(line);
            }
        } catch (IOException ignore) {}



    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meme_photo, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final String imageUrl = imageUrls.get(position);
        try {
            picasso
                    .load(imageUrl)
                    .into(holder.view);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new ImageSelectedEvent(imageUrl));
                }
            });

        } catch (Exception e) {
            throw new RuntimeException((e));
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public ImageView view;

        public Holder(View itemView) {
            super(itemView);
            view = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public static class ImageSelectedEvent {
        private final String imageUrl;

        public ImageSelectedEvent(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
