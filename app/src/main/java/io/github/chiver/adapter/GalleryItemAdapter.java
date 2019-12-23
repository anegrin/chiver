package io.github.chiver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cz.fhucho.android.util.SimpleDiskCache;
import io.github.chiver.ItemActivity;
import io.github.chiver.R;
import io.github.chiver.util.CachingImageLoader;
import io.github.chiver.model.GalleryItem;

public class GalleryItemAdapter extends RecyclerView.Adapter<GalleryItemAdapter.GalleryViewHolder> {
    private final Context context;
    private final List<GalleryItem> galleryItems;
    private final SimpleDiskCache simpleDiskCache;
    private ImageLoader imageLoader;

    public GalleryItemAdapter(List<GalleryItem> galleryItems, Context context, SimpleDiskCache simpleDiskCache) {
        this.galleryItems = galleryItems;
        this.context = context;
        this.simpleDiskCache = simpleDiskCache;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_gallery_item, parent, false);
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem galleryItem = galleryItems.get(position);

        holder.play.setVisibility(galleryItem.type == GalleryItem.Type.ANIMATED ? View.VISIBLE : View.INVISIBLE);

        ImageLoader imageLoader = getImageLoader();

        imageLoader.get(galleryItem.imageSource, ImageLoader.getImageListener(holder.image, 0, 0));
        holder.image.setImageUrl(galleryItem.imageSource, imageLoader);

        holder.itemView.setOnClickListener(v -> onClick(position));

    }

    private void onClick(int position) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.putExtra(ItemActivity.URLS_KEY, galleryItems.stream().map(gi -> gi.imageSource).toArray(String[]::new));
        intent.putExtra(ItemActivity.POS_KEY, position);
        context.startActivity(intent);

    }

    private synchronized ImageLoader getImageLoader() {

        if (this.imageLoader == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            this.imageLoader = new ImageLoader(requestQueue, new CachingImageLoader(simpleDiskCache));
        }

        return this.imageLoader;
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private final NetworkImageView image;
        private final ImageView play;

        GalleryViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.niv_gallery_item);
            play = itemView.findViewById(R.id.iv_play);
        }
    }
}
