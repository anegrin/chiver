package io.github.chiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;
import io.github.chiver.adapter.GalleryItemAdapter;
import io.github.chiver.model.Gallery;

public class GalleryActivity extends BaseActivity {

    public static final String GALLERY_KEY = "gallery";

    private Gallery gallery;

    public GalleryActivity() {
        super(false);
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        gallery = getIntent().getParcelableExtra(GALLERY_KEY);
        RecyclerView recyclerView = findViewById(R.id.rv_gallery_items);
        recyclerView.setHasFixedSize(true);
        GalleryItemAdapter adapter = new GalleryItemAdapter(gallery.galleryItems, this, getChiver().getSimpleDiskCache());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void _onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.mi_refresh).setVisible(false);
        menu.findItem(R.id.mi_share).setVisible(false);
        menu.findItem(R.id.mi_browse).setVisible(true);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery;
    }

    @Override
    protected boolean onBrowse(MenuItem item) {
        if (gallery != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gallery.link)));
        }

        return super.onShare(item);
    }
}
