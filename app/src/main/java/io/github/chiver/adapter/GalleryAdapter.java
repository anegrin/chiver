package io.github.chiver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cz.fhucho.android.util.SimpleDiskCache;
import io.github.chiver.GalleryActivity;
import io.github.chiver.R;
import io.github.chiver.model.Gallery;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private final Context context;
    private final List<Gallery> items;
    private final SimpleDiskCache simpleDiskCache;
    private ImageLoader imageLoader;

    public GalleryAdapter(Context context, SimpleDiskCache simpleDiskCache) {
        this.context = context;
        this.simpleDiskCache = simpleDiskCache;
        items = new LinkedList<>();
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        Gallery gallery = items.get(position);

        holder.title.setText(gallery.title);

        ImageLoader imageLoader = getImageLoader();

        imageLoader.get(gallery.imageSource, ImageLoader.getImageListener(holder.image, 0,0));
        holder.image.setImageUrl(gallery.imageSource, imageLoader);

        holder.itemView.setOnClickListener(v -> onClick(gallery));
    }

    private void onClick(Gallery gallery) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(GalleryActivity.GALLERY_KEY, gallery);
        context.startActivity(intent);
    }

    private synchronized ImageLoader getImageLoader() {

        if (this.imageLoader == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            this.imageLoader = new ImageLoader(requestQueue,
                    new ImageLoader.ImageCache() {
                        @Override
                        public Bitmap getBitmap(String url) {
                            try {
                                if (simpleDiskCache.contains(url)) {
                                    return simpleDiskCache.getBitmap(url).getBitmap();
                                } else {
                                    return null;
                                }
                            } catch (IOException ioe) {
                                throw new RuntimeException(ioe);
                            }
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            try {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
                                byte[] imageArray = out.toByteArray();
                                simpleDiskCache.put(url, new ByteArrayInputStream(imageArray));
                            } catch (IOException ioe) {
                                throw new RuntimeException(ioe);
                            }
                        }

                    });
        }

        return this.imageLoader;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Gallery gallery) {
        if (!items.contains(gallery)) {
            items.add(gallery);
        }
    }

    public void clearItems() {
        items.clear();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final NetworkImageView image;

        GalleryViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_gallery);
            image = itemView.findViewById(R.id.niv_gallery);
        }
    }
}
